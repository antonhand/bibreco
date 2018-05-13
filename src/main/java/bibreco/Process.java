package bibreco;

import bibreco.features.Feature;
import bibreco.model.Author;
import bibreco.model.Bibliography;
import bibreco.model.Record;
import com.cybozu.labs.langdetect.DetectorFactory;
import com.cybozu.labs.langdetect.LangDetectException;
import org.grobid.core.data.BibDataSet;
import org.grobid.core.engines.Engine;
import org.grobid.core.factory.GrobidFactory;
import org.grobid.core.main.GrobidHomeFinder;
import org.grobid.core.utilities.GrobidProperties;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.time.Year;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Process {

    private static Engine engine;

    public Process() throws IOException {
        Logger.getLogger("org.grobid").setLevel(Level.SEVERE);
        loadLanguages();
        Properties prop = new Properties();
        prop.load(new FileInputStream("src/main/resources/diploma.properties"));
        String pGrobidHome = prop.getProperty("pGrobidHome");

        GrobidHomeFinder grobidHomeFinder = new GrobidHomeFinder(Arrays.asList(pGrobidHome));

        GrobidProperties.getInstance(grobidHomeFinder);

        engine = GrobidFactory.getInstance().createEngine();

    }

    private static void loadLanguages(){
        if (DetectorFactory.getLangList().isEmpty()) {
            try {
                DetectorFactory.loadProfile("src/main/resources/profiles");
            } catch (LangDetectException e) {

            }
        }
    }

    public static Record textToRecord(String input) {
        loadLanguages();
        input = input.replaceAll(" \\(ред\\.\\)| \\(ed\\.\\)| и др\\.| [ea]t al\\.| and others| \\(eds.\\)", "");
        String[] splt = input.split(", | // |, and |; ");

        int mode = 0;
        LinkedHashSet<Author> seta = new LinkedHashSet<>();

        Record r = new Record();
        String tmp = "";
        Integer ord = 0;
        Pattern yearpat =  Pattern.compile("^[\\W\\d]*\\(?(\\d{4})(\\)|$)", Pattern.UNICODE_CASE | Pattern.UNICODE_CHARACTER_CLASS);
        for (String st : splt) {

            switch (mode) {
                case 0:
                    Author a = textToAuthor(st, ord);

                    if(a != null){
                        seta.add(a);
                        continue;
                    }

                    String[] spl = st.split(" and | и ");


                    if(spl.length == 2){
                        a = textToAuthor(spl[0], ord);
                        Author a1 = textToAuthor(spl[1], ord);
                        if(a != null && a1!= null){
                            seta.add(a);
                            seta.add(a1);
                            continue;
                        }


                    }

                    if (seta.size() == 0) {
                        return null;
                    }
                    r.setAuthors(seta);

                    if (st.startsWith("“") || st.startsWith("\"")) {
                        Matcher title = Pattern.compile("^[“\"](.+)[”\"]", Pattern.UNICODE_CASE).matcher(st);
                        if(title.find()){
                            r.setTitle(title.group(1));
                            mode = 2;
                        } else {
                            tmp = st.substring(1);
                            mode = 1;
                        }

                    } else {
                        r.setTitle(st);
                        mode = 2;
                    }
                    break;
                case 1:
                    Matcher title = Pattern.compile("(.+)[”\"]", Pattern.UNICODE_CASE).matcher(st);
                    if (title.find()) {
                        tmp += ", " + title.group(1);
                        r.setTitle(tmp);
                        mode = 2;

                    } else {
                        tmp += ", " + st;
                    }
                    break;
                case 2:
                    Matcher year = yearpat.matcher(st);
                    if (year.find() && Year.now().getValue() >= Integer.parseInt(year.group(1)) && Integer.parseInt(year.group(1)) >= 1500) {
                        r.setYear(Integer.parseInt(year.group(1)));
                        r.setJournal(r.getTitle());
                        r.setTitle("");
                        return r;
                    }
                    r.setJournal(st);
                    mode = 3;
                    break;
                case 3:
                    year = yearpat.matcher(st);
                    if (year.find() && Year.now().getValue() >= Integer.parseInt(year.group(1)) && Integer.parseInt(year.group(1)) >= 1500) {
                        r.setYear(Integer.parseInt(year.group(1)));
                        return r;
                    }

            }
        }
		if(mode < 2){
            return null;
        }
        return r;
    }

    public static Bibliography textsToBibliography(List<String> input) {
        loadLanguages();
        Bibliography bib = new Bibliography();
        for (String i : input) {
            Record r = textToRecord(i);
            if (r != null) {
                bib.add(r);
            } /*else {
                System.out.println(i);
            }*/
        }
        return bib;
    }

    private static Author textToAuthor(String input, Integer ord) {
        String prefix = "[фФ]он |[дД]е |[Вв]ан |[дД]е [лЛ]а |[вВ]ан [дД]ер |[vV]on |[dD]e |[vV]an |[dD]e [lL]a |[vV]an [dD]er ";
        if(ord >= 0) {
            Matcher author = Pattern.compile("^([\\w\\-]+?\\.)(?:[ -]?([\\w\\-]+?\\.))? ((?:" + prefix + ")?[\\w\\-']+)$", Pattern.UNICODE_CASE | Pattern.UNICODE_CHARACTER_CLASS).matcher(input);
            if (author.find()) {
                Author a = new Author(author.group(3), author.group(1), author.group(2));
                ord = 1;
                return a;
            }
        }
        if(ord <= 0) {
            Matcher author = Pattern.compile("^((?:" + prefix + ")?[\\w\\-']+) ([\\w\\-]+?\\.)(?:[ -]?([\\w\\-]+?\\.))?$", Pattern.UNICODE_CASE | Pattern.UNICODE_CHARACTER_CLASS).matcher(input);
            if (author.find()) {
                Author a = new Author(author.group(1), author.group(2), author.group(3));
                ord = -1;
                return a;
            }
        }
        return null;
    }


    public static Set<Record> recommend(List<Bibliography> bibs, List<Feature> features) {
        Set<Record> s = new HashSet<>();
        for (int i = 0; i < bibs.size(); i++) {
            s.addAll(bibs.get(i).getData());
        }

        for (Feature feature : features) {
            feature.process(s, bibs);
        }
        Set<Record> s1 = new TreeSet<>(s);
        return s1;
    }

    public Bibliography bibliographyFromPdf(String path) throws Exception {

        List<BibDataSet> articles = engine.processReferences(new File(path), false);
        List<String> lst = new ArrayList<>();
        for (BibDataSet i : articles) {
            lst.add(i.getRawBib().replaceAll("\n|-\n", ""));
        }

        return textsToBibliography(lst);
    }

    public List<Bibliography> bibliographiesFromPdfs(String directory) throws Exception {
        File dir = new File(directory);
        if(!dir.isDirectory()){
            return null;
        }
        List<Bibliography> bibs = new ArrayList<>();

        for(File f : dir.listFiles()){
            if(f.getPath().endsWith(".pdf")){
                bibs.add(this.bibliographyFromPdf(f.getPath()));
            }
        }
        return bibs;
    }

}
