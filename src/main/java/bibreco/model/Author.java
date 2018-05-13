package bibreco.model;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import javax.net.ssl.HttpsURLConnection;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;


public class Author {
	private String surname;
    private String name;
    private String midname;
    
    private String __surname;
    private String __name;
    private String __midname;
    private int __hashcode;
    private String rusletters = ".*[йцукенгшщзхъфывапролджэячсмитьбюёЙЦУКЕНГШЩЗХЪФЫВАПРОЛДЖЭЯЧСМИТЬБЮЁ].*";
    
    public Author() { }

    public Author(String surname, String name, String midname) {
        this.surname = surname;
        this.name = name;
        this.midname = midname;
        this.__midname = midname == null || midname == ""? "" : translate(midname);
        this.__surname = translate(surname);
        this.__name = translate(name);
        this.__hashcode = this.hash();
    }

    private String translate(String input){
        try {
            if (input.matches(rusletters)) {
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
    
    
    @Override
	public String toString() {
		return this.getFullname();
	}

	/**
     * @return Фамилия
     */
    public String getSurname() {
        return surname;
    }
    
    /**
     * @return Имя или инициал
     */
    public String getName() {
        return name;
    }
    
    
	/**
	 * @return Отчество или инициал
	 */
	public String getMidname() {
	    return midname;
	}
	
	/**
	 * @return Фамилия Имя Отчество 
	 */
	public String getFullname() {
		
	    return midname != null? String.join(" ", surname, name, midname): String.join(" ", surname, name);
	}
	
    
	@Override
	public int hashCode() {
		return __hashcode;
	}

    private int hash() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((__midname == null) ? 0 : __midname.hashCode());
        result = prime * result + ((__name == null) ? 0 : __name.hashCode());
        result = prime * result + ((__surname == null) ? 0 : __surname.hashCode());
        return result;
    }

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Author other = (Author) obj;
		if (other.__hashcode != this.__hashcode)
		    return false;
		return true;
	}

	public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setName(String name) {
        this.name = name;
    }
    
    public void setMidname(String midname) {
    	this.midname = midname;
	}

}
