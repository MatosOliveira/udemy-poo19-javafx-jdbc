/**
 * 
 */
package model.dao;

import db.Database;
import model.dao.impl.DepartmentDaoJDBC;
import model.dao.impl.SellerDaoJDBC;

/**
 * Classe - Factory
 * 
 * @author Matos - 25.06.2023
 *
 */
public class DaoFactory {

	public static SellerDao createSellerDao() {
		return new SellerDaoJDBC(Database.getConnection());
	}
	
	public static DepartmentDao createDepartmentDao() {
		return new DepartmentDaoJDBC(Database.getConnection());
	}
}
