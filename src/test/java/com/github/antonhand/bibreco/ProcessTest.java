package bibreco;

import bibreco.features.AuthorsRefsCount;
import bibreco.features.Feature;
import bibreco.features.JournalRefsCount;
import bibreco.features.TitleSimilarity;
import bibreco.model.Author;
import bibreco.model.Bibliography;
import bibreco.model.Record;
import junit.framework.TestCase;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ProcessTest extends TestCase {

    public void testTextToRecord() {
        Record r = Process.textToRecord("Е. И. Бережной, “Об одной теореме Г. Я. Лозановского”, Изв. вузов. Матем., 1982, № 2, 81–83");
        Author a  = r.getAuthors().toArray(new Author[r.getAuthors().size()])[0];
        assertEquals(a.getSurname(), "Бережной");
        assertEquals(a.getName(), "Е.");
        assertEquals(a.getMidname(), "И.");
        assertEquals(r.getTitle(), "Об одной теореме Г. Я. Лозановского");
        assertEquals(r.getJournal(), "Изв. вузов. Матем.");
        assertEquals(r.getYear(), 1982);

        r = Process.textToRecord("Криводубский О.А., Новаковская А.О., Математическая модель восстановления активных свойств сор-бента, Iнформатика та обчислювальна технi-ка: сб. наук. пр. Донецького нац. технiчного ун-ту, 2009, № 10 (153), 251–254");
        a  = r.getAuthors().toArray(new Author[r.getAuthors().size()])[0];
        assertEquals(a.getSurname(), "Криводубский");
        assertEquals(a.getName(), "О.");
        assertEquals(a.getMidname(), "А.");
        a  = r.getAuthors().toArray(new Author[r.getAuthors().size()])[1];
        assertEquals(a.getSurname(), "Новаковская");
        assertEquals(a.getName(), "А.");
        assertEquals(a.getMidname(), "О.");

        assertEquals(r.getTitle(), "Математическая модель восстановления активных свойств сор-бента");
        assertEquals(r.getJournal(), "Iнформатика та обчислювальна технi-ка: сб. наук. пр. Донецького нац. технiчного ун-ту");
        assertEquals(r.getYear(), 2009);
    }

    public void testTextsToBibliography() {
        List<String> s = new ArrayList<>();
		s.add("C. B. Morrey, On the solutions of quasi-linear elliptic partial differential equations, Trans. Amer. Math. Soc., 43:1 (1938), 126–166");
		s.add("V. I. Burenkov, “Recent progress in studying the boundedness of classical operators of real analysis in general Morrey-type spaces. I”, Eurasian Math. J., 3:3 (2012), 11–32");

        Bibliography b = Process.textsToBibliography(s);

        ArrayList<Record> recs = new ArrayList<>(b.getData());

        Record r = recs.get(0);
        Author a  = r.getAuthors().toArray(new Author[r.getAuthors().size()])[0];
        assertEquals(a.getSurname(), "Morrey");
        assertEquals(a.getName(), "C.");
        assertEquals(a.getMidname(), "B.");
        assertEquals(r.getTitle(), "On the solutions of quasi-linear elliptic partial differential equations");
        assertEquals(r.getJournal(), "Trans. Amer. Math. Soc.");
        assertEquals(r.getYear(), 1938);

        r = recs.get(1);
        a  = r.getAuthors().toArray(new Author[r.getAuthors().size()])[0];
        assertEquals(a.getSurname(), "Burenkov");
        assertEquals(a.getName(), "V.");
        assertEquals(a.getMidname(), "I.");
        assertEquals(r.getTitle(), "Recent progress in studying the boundedness of classical operators of real analysis in general Morrey-type spaces. I");
        assertEquals(r.getJournal(), "Eurasian Math. J.");
        assertEquals(r.getYear(), 2012);

    }

    public void testRecommend(){

        List<String> s = new ArrayList<>();
		s.add("C. B. Morrey, “On the solutions of quasi-linear elliptic partial differential equations”, Trans. Amer. Math. Soc., 43:1 (1938), 126–166");
		s.add("V. I. Burenkov, “Recent progress in studying the boundedness of classical operators of real analysis in general Morrey-type spaces. I”, Eurasian Math. J., 3:3 (2012), 11–32");
		s.add("V. I. Burenkov, “Recent progress in studying the boundedness of classical operators of real analysis in general Morrey-type spaces. II”, Eurasian Math. J., 4:1 (2013), 21–45");
		s.add("V. I. Burenkov, P. Jain, T. V. Tararykova, “On boundedness of the Hardy operator in Morrey-type spaces”, Eurasian Math. J., 2:1 (2011), 52–80");
		s.add("Е. Д. Нурсултанов, В. И. Буренков, Д. К. Чигамбаева, “Описание интерполяционных пространств для пары локальных пространств типа Морри и их обобщений”, Функциональные пространства и смежные вопросы анализа, Сборник статей. К 80-летию со дня рождения члена-корреспондента РАН Олега Владимировича Бесова, Тр. МИАН, 284, МАИК, М., 2014, 105–137 ; англ. пер.: V. I. Burenkov, E. D. Nursultanov, D. K. Chigambayeva, “Description of the interpolation spaces for a pair of local Morrey-type spaces and their generalizations”, Proc. Steklov Inst. Math., 284 (2014), 97–128");

		List<String> s1 = new ArrayList<>();
		s1.add("C. B. Morrey, “On the solutions of quasi-linear elliptic partial differential equations”, Trans. Amer. Math. Soc., 43:1 (1938), 126–166");
		s1.add("A. Gogatishvili, R. Mustafayev, “Dual spaces of local Morrey-type spaces”, Czechoslovak Math. J., 61(136):3 (2011), 609–622");
		s1.add("A. Gogatishvili, R. Ch. Mustafayev, “New characterization of Morrey spaces”, Eurasian Math. J., 4:1 (2013), 54–64");
		s1.add("Е. И. Бережной, “Об одной теореме Г. Я. Лозановского”, Изв. вузов. Матем., 1982, № 2, 81–83 ; англ. пер.: E. I. Berezhnoĭ, “On a theorem of G. Ya. Lozanovskiǐ”, Soviet Math. (Iz. VUZ), 26:2 (1982), 107–111");
		s1.add("O. Blasco, A. Ruiz, L. Vega, “Non interpolation in Morrey–Companato and block spaces”, Ann. Scuola Norm. Sup. Pisa Cl. Sci. (4), 28:1 (1999), 31–40");
		s1.add("A. Ruiz, L. Vega, “Corrigenda to “Unique continuation for Schrödinger operators” and a remark on interpolation of Morrey spaces”, Publ. Mat., 39:2 (1995), 405–411");
		s1.add("В. И. Буренков, Е. Д. Нурсултанов, Д. К. Чигамбаева, “Описание интерполяционных пространств для пары локальных пространств типа Морри и их обобщений”, Функциональные пространства и смежные вопросы анализа, Сборник статей. К 80-летию со дня рождения члена-корреспондента РАН Олега Владимировича Бесова, Тр. МИАН, 284, МАИК, М., 2014, 105–137 ; англ. пер.: V. I. Burenkov, E. D. Nursultanov, D. K. Chigambayeva, “Description of the interpolation spaces for a pair of local Morrey-type spaces and their generalizations”, Proc. Steklov Inst. Math., 284 (2014), 97–128");

        List<Bibliography> bibs = new ArrayList<>();
        bibs.add(Process.textsToBibliography(s));
        bibs.add(Process.textsToBibliography(s1));

        List<Feature> fs = new ArrayList<>();
        Feature feature = new AuthorsRefsCount(4);
        fs.add(feature);
        feature = new JournalRefsCount(4);
        fs.add(feature);

        Set<Record> recs = Process.recommend(bibs, fs);


        String[] cor = {"8.0 Burenkov V. I., Jain P., Tararykova T. V., “On boundedness of the Hardy operator in Morrey-type spaces”, Eurasian Math. J., 2011",
                        "8.0 Burenkov V. I., “Recent progress in studying the boundedness of classical operators of real analysis in general Morrey-type spaces. I”, Eurasian Math. J., 2012",
                        "8.0 Burenkov V. I., “Recent progress in studying the boundedness of classical operators of real analysis in general Morrey-type spaces. II”, Eurasian Math. J., 2013",
                        "6.0 Gogatishvili A., Mustafayev R. Ch., “New characterization of Morrey spaces”, Eurasian Math. J., 2013",
                        "5.0 Нурсултанов Е. Д., Буренков В. И., Чигамбаева Д. К., “Описание интерполяционных пространств для пары локальных пространств типа Морри и их обобщений”, Функциональные пространства и смежные вопросы анализа, 2014",
                        "3.0 Ruiz A., Vega L., “Corrigenda to “Unique continuation for Schrödinger operators” and a remark on interpolation of Morrey spaces”, Publ. Mat., 1995",
                        "3.0 Gogatishvili A., Mustafayev R., “Dual spaces of local Morrey-type spaces”, Czechoslovak Math. J., 2011",
                        "3.0 Blasco O., Ruiz A., Vega L., “Non interpolation in Morrey–Companato and block spaces”, Ann. Scuola Norm. Sup. Pisa Cl. Sci. (4), 1999",
                        "2.0 Morrey C. B., “On the solutions of quasi-linear elliptic partial differential equations”, Trans. Amer. Math. Soc., 1938",
                        "2.0 Бережной Е. И., “Об одной теореме Г. Я. Лозановского”, Изв. вузов. Матем., 1982"};
        int i = 0;
        for (Record rec : recs) {
            assertEquals(rec.getRank() + " " + rec, cor[i]);
            i++;
        }

    }

    public void testBibliographyFromPdf() throws Exception {
        Process pr = new Process();

        Bibliography b = pr.bibliographyFromPdf("src/test/resources/im2628.pdf");

        ArrayList<Record> recs = new ArrayList<>(b.getData());

        assertEquals(recs.size(), 7);

        Record r = recs.get(0);
        Author a  = r.getAuthors().toArray(new Author[r.getAuthors().size()])[0];
        assertEquals(a.getSurname(), "Погорелов");
        assertEquals(a.getName(), "А.");
        assertEquals(a.getMidname(), "В.");
        assertEquals(r.getTitle(), "Изгибания поверхностей и устойчивость оболочек");
        assertEquals(r.getJournal(), "Наука");
        assertEquals(r.getYear(), 1986);
        r = recs.get(2);
        a  = r.getAuthors().toArray(new Author[r.getAuthors().size()])[0];
        assertEquals(a.getSurname(), "Штогрин");
        assertEquals(a.getName(), "М.");
        assertEquals(a.getMidname(), "И.");
        assertEquals(r.getTitle(), "Изометрические вложения поверхностей платоновых тел");
        assertEquals(r.getJournal(), "УМН");
        assertEquals(r.getYear(), 2007);
    }

}