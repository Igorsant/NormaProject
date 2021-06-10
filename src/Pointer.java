import java.util.ArrayList;

public class Pointer {

	private int index;
	private String label;
	
	public Pointer(int index, String label) {
		this.index = index;
		this.label = label;
	}
	
	public static Pointer getPointer(ArrayList<Pointer> pointers, String name) {
		for(Pointer p:pointers) {
			if(p.getLabel().equals(name)) {
				return p;
			}
		}
		return null;
	}
	
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getLabel() {
		return label;
	}
	public void setLabel(String label) {
		this.label = label;
	}
	
	
}
