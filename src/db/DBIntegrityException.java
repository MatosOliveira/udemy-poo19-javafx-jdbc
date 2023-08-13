/**
 * 
 */
package db;

/**
 * @author Matos
 *
 */
public class DBIntegrityException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public DBIntegrityException(String mensagem) {
		super(mensagem);
	}
}
