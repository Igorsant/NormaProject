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

		System.out.println("=========================================");
		if(stack.size() > 0) {
			System.out.println("Não compilou");
			return;
		}
		execute(0);
		System.out.println("=========================================");
		for(int i = 0; i < registradores.length; i++) {
			System.out.println("Registrador " + i + " = " + registradores[i]);
		}
	}

	private void execute(int start) {
		for(int i = start; i < lang.size(); i++){
			System.out.println("\n" + lang.get(i) + "<<<<<<");
			for(int j = 0; j < registradores.length; j++) {
				System.out.println("Registrador " + j + " = " + registradores[j]);
			}
			String[] aux = lang.get(i).trim().split(" ");
			if(!execFactory(aux, i)) {
				break;
			}

		}

		System.out.println("\n=========================================");
		for(int j = 0; j < registradores.length; j++) {
			System.out.println("Registrador " + j + " = " + registradores[j]);
		}
	}

	private boolean execFactory(String[] values, int index) {
		String command = values[0];

		if(command.equals(Tag.inc)) {
			registradores[Integer.parseInt(values[1])]++;
			return true;
		}

		if(command.equals(Tag.dec)) {
			registradores[Integer.parseInt(values[1])]-=1;
			return true;
		}

		if(command.equals(Tag.set0)) {
			registradores[Integer.parseInt(values[1])] = 0;
			return true;
		}

		if(command.equals(Tag.ifs) && values[1].equals(Tag.is0)) {
			int registrador = registradores[Integer.parseInt(values[2])];
			if(registrador != 0) {
				execute(findNext(index, Tag.endif, this.getNumberOfBlankSpaces(index)));
				return true;
			}
		}

		if(command.equals(Tag.elses)) {
			String[] line = findIfLine(index, this.getNumberOfBlankSpaces(index));
			if(line != null && line[1].equals(Tag.is0)) {
				int registrador = registradores[Integer.parseInt(line[2])];
				if(registrador == 0) {
					execute(findNext(index, Tag.endelse, this.getNumberOfBlankSpaces(index)));
					return true;
				}
			}
		}

		if(command.equals(Tag.gotos)) {
			Pointer p = Pointer.getPointer(this.pointers, values[1]);
			if (p != null) {
				execute(p.getIndex());
				return true;
			}
		}

		if(command.equals(Tag.sets)) {
			int registradorPos = Integer.parseInt(values[1]);
			int novoValorRegistrador = Integer.parseInt(values[2]);

			if (registradorPos < 0 || registradorPos > registradores.length) return false;
			if (novoValorRegistrador < 0) return false;

			// Antes de atribuir, verifica se a posição do registrador é válida ou se o valor é positivo
			registradores[registradorPos] = novoValorRegistrador;
			return true;
		}

		if (command.equals(Tag.adds)) {
			int registradorOrigemPos, registradorValorPos;
			registradorOrigemPos = Integer.parseInt(values[1]);

			if (registradorOrigemPos < 0 || registradorOrigemPos > registradores.length) return false;

			registradorValorPos = Integer.parseInt(values[2]);
			if (registradorValorPos < 0) return false;

			// Antes de somar, verifica se a posição do registrador é válida ou se o valor é negativo
			registradores[registradorOrigemPos] += registradores[registradorValorPos];
			return true;
		}

		if(command.equals(Tag.whiles)) {
			int registrador = registradores[Integer.parseInt(values[1])];

			if (registrador > 0) {
				registradores[Integer.parseInt(values[1])]--;
				execute(index);
			}
			execute(findNext(index, Tag.endwhiles, this.getNumberOfBlankSpaces(index)) + 1);
			return true;
		}

		if(command.equals(Tag.fors)) {
			// for
			return true;
		}

		return false;
	}

	private String[] findIfLine(int index, int numberOfBlankSpaces) {
		for(int i = index; i > 0; i--) {
			String[] line = lang.get(i).trim().split(" ");
			if(line[0].equals(Tag.ifs) && numberOfBlankSpaces == this.getNumberOfBlankSpaces(i)) {
				return line;
			}
		}
		return null;
	}

	private int getNumberOfBlankSpaces(int index) {
		char[] chars = lang.get(index).toCharArray();
		int counter = 0;
		for(char c: chars) {
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
		String command = values[0];

		if(command.startsWith(":")) {
			String name = command.substring(1);
			pointers.add(new Pointer(index, name));
		}

		if(command.equals(Tag.ifs)) {
			try {
				stack.push(Tag.ifs);
			} catch (Exception e) {
				return true;
			}
		}

		if(command.equals(Tag.endif)) {
			try {
				if(!stack.pop().equals(Tag.ifs)) {
					return true;
				}
			} catch (Exception e) {
				return true;
			}
		}

		if(command.equals(Tag.elses) && getAnterior(index-1).equals(Tag.endif)) {
			try {
				stack.push(Tag.elses);

			} catch(Exception e) {
				return true;
			}
		}

		if(command.equals(Tag.endelse)) {
			try {
				if(!stack.pop().equals(Tag.elses)) {
					return true;
				}
			} catch (Exception e) {
				return true;
			}
		}

		if(command.equals(Tag.whiles)) {
			try {
				stack.push(Tag.whiles);

			} catch(Exception e) {
				return true;
			}
		}

		if(command.equals(Tag.endwhiles)) {
			try {
				if(!stack.pop().equals(Tag.whiles)) {
					return true;
				}
			} catch (Exception e) {
				return true;
			}
		}

		return false;
	}

	private String getAnterior(int index) {
		String value = lang.get(index).trim().split(" ")[0];
		if(value.equals("")) {
			return getAnterior(index-1);
		} else {
			return value;
		}
	}

	private int findNext(int start, String end, int numberOfBlankSpaces) {
		for(int i=start; i<lang.size(); i++) {
			if(lang.get(i).trim().split(" ")[0].equals(end) && this.getNumberOfBlankSpaces(i) == numberOfBlankSpaces) {
				return i+1;
			}
		}
		return -1;
	}
}