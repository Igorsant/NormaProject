import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Norma {
	
	private int[] registradores;
	private ArrayList<Pointer> pointers;
	private ArrayList<String> lang;
	
	public Norma() {
		this.registradores = new int[30];
		this.pointers = new ArrayList<>();
		this.lang = new ArrayList<>();
	}

	public void execute() throws Exception{
		BufferedReader br = new BufferedReader(new FileReader("src/lang.txt"));
		String line;
		int counter = 0;
		while((line = br.readLine()) != null) {	
			String[] aux = line.split(" ");
			creatingPointers(aux, counter);
			System.out.println(line.trim());
			lang.add(line.trim());
			counter++;
		}
		execute(0);
		/*for(int i=0; i<registradores.length; i++) {
			System.out.println("Registrador "+i+" = "+registradores[i]);
		}*/
	}
	
	public void execute(int start) {
		for(int i=start; i<lang.size(); i++){
			String[] aux = lang.get(i).split(" ");
			if(execFactory(aux)) {
				break;
			}
		}
		
	}
	
	public boolean execFactory(String[] values) {
		
		if(values[0].equals("inc")) {
			registradores[Integer.parseInt(values[1])]++;
		}
		if(values[0].equals("dec")) {
			registradores[Integer.parseInt(values[1])]--;
		}
		if(values[0].equals("set0")) {
			registradores[Integer.parseInt(values[1])] = 0;
		}
		if(values[0].equals("if") && values[1].equals("is0")) {
			int registrador = registradores[Integer.parseInt(values[2])];
			if(registrador == 0) {
				
			}
			
		}
		
		if(values[0].equals("goto")) {
			Pointer p = Pointer.getPointer(this.pointers, values[1]);
			execute(p.getIndex());
			return true;
		}
		return false;
	}
	
	public void creatingPointers(String[] values, int index) {
		if(values[0].startsWith(":")) {
			String name = values[0].substring(1, values[0].length());
			pointers.add(new Pointer(index, name));
		}
	}
	
	
}
