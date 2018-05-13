package bibreco.model;

import java.util.Iterator;
import java.util.LinkedHashSet;
public class Bibliography {
    private LinkedHashSet<Record> data;
    
    public Bibliography(){
    	data = new LinkedHashSet<Record>();
    }

	@Override
	public String toString() {
		String st = new String();
		Iterator<Record> it = data.iterator();
		
		for (int i = 0; it.hasNext(); i++){
			Record rec = it.next();
			st += i + 1;
			st += ". ";
			st += rec + (rec.toString().endsWith(".")? "" : ".") + "\n";
		}
		return st;
	}

	public void add(Record r) {
		data.add(r);
	}
	
	public boolean contains(Record r) {
		return data.contains(r);
	}

	/**
	 * @return the data
	 */
	public LinkedHashSet<Record> getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(LinkedHashSet<Record> data) {
		this.data = data;
	}
}
