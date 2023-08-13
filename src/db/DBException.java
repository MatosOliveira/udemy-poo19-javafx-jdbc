/**
 * 
 */
package db;

/**
 * Classe - Exception
 * @author Matos - 24.06.2023
 *
 */
public class DBException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	public DBException(String mensagem) {
		super(mensagem);
	}

}
