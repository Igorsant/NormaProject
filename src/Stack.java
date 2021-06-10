public class Stack<E> {

    private static final int standardSize = 10;
    private final int size; // Número máximo de elementos
    private int top;        // Topo da pilha
    private E[] elements;   // Array de armazenamento
    private int tamanho;

    public Stack(int size) {
    	this.tamanho = 0;
        this.size = size > 0 ? size : standardSize;
        top = -1;
        elements = (E[]) new Object[size];
    }

    public Stack() {
        this(standardSize);
    }

    /*
     * Insere elemento na pilha, se bem sucedido,
     * caso contrário, lança um FullStackException
     */
    public void push(E pushValue) throws Exception{
        // Verifica se a pilha está cheia
        if (top == size - 1) {
            throw new Exception("Stack is full");
        }
        // Insere o pushValue na pilha
        elements[++top] = pushValue;
        tamanho++;
    }

    /*
     * Retorna o elemento superior se a pilha não estiver vazia,
     * caso contrário lança uma EmptyStackException
     */
    public E pop() throws Exception{
        // Verifica se a pilha está vazia
        if (top == -1) {
            throw new Exception("Stack is empty, cannot pop");
        }
        // Remove o elemento superior da pilha
        tamanho--;
        return elements[top--];
    }
    
    public int size() {
    	return tamanho;
    }
}