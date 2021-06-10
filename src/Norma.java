import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Norma {
	
	private int[] registradores;
	private ArrayList<Pointer> pointers;
	private ArrayList<String> lang;
	private Stack<String> stack;
	
	public Norma() {
		this.registradores = new int[30];
		this.pointers = new ArrayList<>();
		this.lang = new ArrayList<>();
		stack = new Stack<>();
	}

	public void execute() throws Exception{
		BufferedReader br = new BufferedReader(new FileReader("src/lang.txt"));
		String line;
		int counter = 0;
		while((line = br.readLine()) != null) {	
			String[] aux = line.trim().split(" ");
			if(compiling(aux, counter)) {
				System.out.println("Não compilou");
				return;
			}
			System.out.println(line.trim());
			lang.add(line.trim());
			counter++;
		}
		if(stack.size() > 0) {
			System.out.println("Não compilou");
			return;
		}
		execute(0);
		for(int i=0; i<registradores.length; i++) {
			System.out.println("Registrador "+i+" = "+registradores[i]);
		}
	}
	
	private void execute(int start) {
		for(int i=start; i<lang.size(); i++){
			String[] aux = lang.get(i).split(" ");
			if(execFactory(aux, i)) {
				break;
			}
		}
		
	}
	
	private boolean execFactory(String[] values, int index) {
		
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
			if(registrador != 0) {
				execute(findNextEndif(index));
				return true;
			}
			
		}
		
		if(values[0].equals("goto")) {
			Pointer p = Pointer.getPointer(this.pointers, values[1]);
			execute(p.getIndex());
			return true;
		}
		return false;
	}
	
	private boolean compiling(String[] values, int index) {
		if(values.length == 0) {
			return false;
		}
		if(values[0].startsWith(":")) {
			String name = values[0].substring(1, values[0].length());
			pointers.add(new Pointer(index, name));
		}
		if(values[0].equals("if")) {
			try {
				stack.push("if");
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return true;
			}
		}
		if(values[0].equals("endif")) {
			try {
				if(!stack.pop().equals("if")) {
					return true;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return true;
			}
		}
		return false;
	}
	
	private int findNextEndif(int start) {
		for(int i=start; i<lang.size(); i++) {
			if(lang.get(i).split(" ")[0].equals("endif")) {
				return i+1; 
			}
		}
		return 1000;
	}
	
	
}
