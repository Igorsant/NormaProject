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
			System.out.println(line);
			lang.add(line);
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
			String[] aux = lang.get(i).trim().split(" ");
			if(execFactory(aux, i)) {
				break;
			}
		}
		
	}

	private boolean execFactory(String[] values, int index) {
		
		if(values[0].equals(Tag.inc)) {
			registradores[Integer.parseInt(values[1])]++;
		}
		if(values[0].equals(Tag.dec)) {
			registradores[Integer.parseInt(values[1])]--;
		}
		if(values[0].equals(Tag.set0)) {
			registradores[Integer.parseInt(values[1])] = 0;
		}
		if(values[0].equals(Tag.ifs) && values[1].equals(Tag.is0)) {
			int registrador = registradores[Integer.parseInt(values[2])];
			if(registrador != 0) {
				execute(findNext(index, Tag.endif, this.getNumberofBlackSpaces(index)));
				return true;
			}
			
		}
		if(values[0].equals(Tag.elses)) {
			String[] line = findIfLine(index, this.getNumberofBlackSpaces(index));
			if(line[1].equals(Tag.is0)) {
				int registrador = registradores[Integer.parseInt(line[2])];
				if(registrador == 0) {
					execute(findNext(index, Tag.endelse, this.getNumberofBlackSpaces(index)));
					return true;
				}
			}
		}
		
		if(values[0].equals(Tag.gotos)) {
			Pointer p = Pointer.getPointer(this.pointers, values[1]);
			execute(p.getIndex());
			return true;
		}
		return false;
	}
	
	private String[] findIfLine(int index, int numberOfBlankSpaces) {
		for(int i=index; i>0; i--) {
			String[] line = lang.get(i).trim().split(" ");
			if(line[0].equals(Tag.ifs) && numberOfBlankSpaces == this.getNumberofBlackSpaces(i)) {
				return line;
			}
		}
		return null;
	}
	
	private int getNumberofBlackSpaces(int index) {
		char[] chars = lang.get(index).toCharArray();
		int counter = 0;
		for(char c:chars) {
			if(c == ' ') {
				counter++;
			}else {
				return counter;
			}
		}
		return counter;
	}

	private boolean compiling(String[] values, int index) {
		if(values.length == 0) {
			return false;
		}
		if(values[0].startsWith(":")) {
			String name = values[0].substring(1, values[0].length());
			pointers.add(new Pointer(index, name));
		}
		if(values[0].equals(Tag.ifs)) {
			try {
				stack.push(Tag.ifs);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return true;
			}
		}
		if(values[0].equals(Tag.endif)) {
			try {
				if(!stack.pop().equals(Tag.ifs)) {
					return true;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return true;
			}
		}
		if(values[0].equals(Tag.elses) && getAnterior(index-1).equals(Tag.endif)) {
			try {
				stack.push(Tag.elses);
				
			}catch(Exception e) {
				return true;
			}
		}
		if(values[0].equals(Tag.endelse)) {
			try {
				if(!stack.pop().equals(Tag.elses)) {
					return true;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				return true;
			}
		}
		return false;
	}
	
	private String getAnterior(int index) {
		String value = lang.get(index).trim().split(" ")[0];
		if(value.equals("")) {
			return getAnterior(index-1);
		}else {
			return value;
		}
	}
	
	private int findNext(int start, String end, int numberOfBlankSpaces) {
		for(int i=start; i<lang.size(); i++) {
			if(lang.get(i).trim().split(" ")[0].equals(end) && this.getNumberofBlackSpaces(i) == numberOfBlankSpaces) {
				return i+1; 
			}
		}
		return -1;
	}
	
	
}