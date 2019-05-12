package bibreco.model;


import com.cybozu.labs.langdetect.Detector;
import com.cybozu.labs.langdetect.DetectorFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;

public class Record implements Comparable<Record> {
	private LinkedHashSet<Author> authors;
	private String title;
	private String journal;
	private int year;
	private double rank = 0;
	private String __title;
    private String __journal;
	private String rusletters = ".*[йцукенгшщзхъфывапролджэячсмитьбюёЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮЁ].*";


    private String translate(String input){
        try {

            Detector detector = DetectorFactory.create();
            detector.append(input);
            boolean notenglish = true;

            try {
                if(detector.detect() == "en")
                    notenglish = false;
            } catch (Exception e){}

            if (input.matches(rusletters) || notenglish) {
                String apikey = "trnsl.1.1.20180506T165412Z.df7ddf5ccb03b1b9.bb685710bc80c3a35474033a0cb67de2fa3d2716";
                String urlStr = "https://translate.yandex.net/api/v1.5/tr.json/translate?key=" + apikey;
                URL urlObj = new URL(urlStr);
                HttpsURLConnection connection = (HttpsURLConnection) urlObj.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoOutput(true);
                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.writeBytes("text=" + URLEncoder.encode(input, "UTF-8") + "&lang=en");

                InputStream response = connection.getInputStream();
                String res = new java.util.Scanner(response).nextLine();
                JSONObject json = (JSONObject) new JSONParser().parse(res);
                return (String) ((JSONArray) json.get("text")).get(0);
            }

        } catch (Exception e) {
            System.err.println(input);
            e.printStackTrace();
        }
        return input;
    }

	/**
	 * @return the year
	 */
	public int getYear() {
		return year;
	}

	/**
	 * @param year the year to set
	 */
	public void setYear(int year) {
		this.year = year;
	}
	
	public LinkedHashSet<Author> getAuthors() {
		return authors;
	}
	
	public void setAuthors(LinkedHashSet<Author> authors) {
		this.authors = authors;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
	    this.title = title;
	    this.__title = title == ""? "" : translate(title);

	}

	public double getRank() {
		return rank;
	}

	public void setRank(double rank) {
		this.rank = rank;
	}
	
	public String getJournal() {
		return journal;
	}

	public void setJournal(String journal) {
	    this.journal = journal;
        this.__journal = journal == ""? "" : translate(journal);
	}

	public String getTranslTitle(){
	    return __title;
    }

    public String getTranslJournal(){
        return __journal;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Record record = (Record) o;
        return year == record.year &&
                Objects.equals(authors, record.authors) &&
                Objects.equals(__title, record.__title) &&
                Objects.equals(__journal, record.__journal);
    }

    @Override
    public int hashCode() {

        return Objects.hash(authors, year, __title, __journal);
    }

    @Override
	public String toString() {
		String st = new String();
		Iterator<Author> author = authors.iterator();
		while(author.hasNext()) {
			st += author.next().getFullname() + ", ";
		}
		String res = st;
		if(title != ""){
			res +=  "“" + title + "”, ";
		}
		return res + journal + ", " + year;
	}

	@Override
	public int compareTo(Record rec) {
		if(this.rank != rec.rank) {
			if (rec.rank > this.rank) {
				return 1;
			} else {
				return -1;
			}
		} else {
			if(rec.title != this.title) {
				return this.title.compareTo(rec.title);
			}
			return this.journal.compareTo(rec.journal);
		}
	}

	public void addToRank(double add) {
		rank += add;
	}
	
	
	
}
