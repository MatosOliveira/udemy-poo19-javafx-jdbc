/**
 * 
 */
package model.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import db.DBException;
import db.Database;
import model.dao.SellerDao;
import model.entities.Department;
import model.entities.Seller;

/**
 * Classe que implementa SellerDAO
 * 
 * @author Matos - 25.06.2023
 *
 */
public class SellerDaoJDBC implements SellerDao {

	private Connection conn;
	
	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MM/yyyy");

	public SellerDaoJDBC(Connection conn) {
		this.conn = conn;
	}


	@Override
	public void insert(Seller obj) {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement("INSERT INTO seller"
					+ "(Name, Email, BirthDate, BaseSalary, DepartmentId)"
					+ "VALUES"
					+ "(?, ?, ?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, Date.valueOf(obj.getBirthDate()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			
			int rowsAffected = st.executeUpdate();
			
			if(rowsAffected > 0) {
				rs = st.getGeneratedKeys();
				if(rs.next()) {
					int id = rs.getInt(1);
					obj.setId(id);
				}
			} else {
				throw new DBException("Unexpected error! No rows affected!");
			}

		} catch (SQLException e) {
			throw new DBException(e.getMessage());
			
		} finally {
			Database.closeStatement(st);
			Database.closeResultSet(rs);
		}

	}

	@Override
	public void update(Seller obj) {
		PreparedStatement st = null;

		try {
			st = conn.prepareStatement("UPDATE seller "
					+ "SET Name = ?, Email = ?, BirthDate = ?, BaseSalary = ?, DepartmentId = ? "
					+ "WHERE Id = ?");
			
			st.setString(1, obj.getName());
			st.setString(2, obj.getEmail());
			st.setDate(3, Date.valueOf(obj.getBirthDate()));
			st.setDouble(4, obj.getBaseSalary());
			st.setInt(5, obj.getDepartment().getId());
			st.setInt(6, obj.getId());
			
			st.executeUpdate();
		
		} catch (SQLException e) {
			throw new DBException(e.getMessage());
			
		} finally {
			Database.closeStatement(st);
		}

	}

	@Override
	public void deleteById(Integer id) {
		
		PreparedStatement st = null;

		try {
			st = conn.prepareStatement("DELETE FROM seller WHERE Id = ? ");
			
			st.setInt(1, id);
			
			st.executeUpdate();
		
		} catch (SQLException e) {
			throw new DBException(e.getMessage());
			
		} finally {
			Database.closeStatement(st);
		}

	}

	@Override
	public Seller findById(Integer id) {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement("SELECT seller.*,department.Name as DepName"
					+ "	FROM seller INNER JOIN department"
					+ "	ON seller.DepartmentId = department.Id"
					+ "	WHERE seller.Id = ?");
			st.setInt(1, id);
			rs = st.executeQuery();
			
			if(rs.next()) {
				Department dep = instantiateDepartment(rs);				
				Seller sel = instantiateSeller(rs, dep);
				return sel;
			}
			return null;

		} catch (SQLException e) {
			throw new DBException(e.getMessage());
			
		} finally {
			Database.closeStatement(st);
			Database.closeResultSet(rs);
		}
		
 
	}

	private Seller instantiateSeller(ResultSet rs, Department dep) throws SQLException {
		Seller sel = new Seller();
		sel.setId(rs.getInt("Id"));
		sel.setName(rs.getString("Name"));
		sel.setEmail(rs.getString("Email"));
		sel.setBaseSalary(rs.getDouble("BaseSalary"));
		sel.setBirthDate(rs.getDate("BirthDate").toLocalDate());
		sel.setDepartment(dep);
		
		return sel;
	}


	private Department instantiateDepartment(ResultSet rs) throws SQLException {
		Department dep = new Department();
		dep.setId(rs.getInt("DepartmentId"));
		dep.setName(rs.getString("DepName"));
		return dep;
	}

	@Override
	public List<Seller> findAll() {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement("SELECT seller.*,department.Name as DepName"
					+ "	FROM seller INNER JOIN department"
					+ "	ON seller.DepartmentId = department.Id"
					+ " ORDER BY NAME");
			
			rs = st.executeQuery();
			
			List<Seller> list = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();
			
			while (rs.next()) {
				
				Department dep = map.get(rs.getInt("DepartmentId"));
				if(dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
				}
							
				Seller sel = instantiateSeller(rs, dep);
				list.add(sel);
			}
			
			return list;
			
		} catch (SQLException e) {
			throw new DBException(e.getMessage());
			
		} finally {
			Database.closeStatement(st);
			Database.closeResultSet(rs);
		}
	}


	@Override
	public List<Seller> findByDepartment(Department department) {
		PreparedStatement st = null;
		ResultSet rs = null;

		try {
			st = conn.prepareStatement("SELECT seller.*,department.Name as DepName"
					+ "	FROM seller INNER JOIN department"
					+ "	ON seller.DepartmentId = department.Id"
					+ "	WHERE Department.Id = ?"
					+ " ORDER BY NAME");
			
			st.setInt(1, department.getId());
			rs = st.executeQuery();
			
			List<Seller> list = new ArrayList<>();
			Map<Integer, Department> map = new HashMap<>();
			
			while (rs.next()) {
				
				Department dep = map.get(rs.getInt("DepartmentId"));
				if(dep == null) {
					dep = instantiateDepartment(rs);
					map.put(rs.getInt("DepartmentId"), dep);
				}
							
				Seller sel = instantiateSeller(rs, dep);
				list.add(sel);
			}
			
			return list;
			
		} catch (SQLException e) {
			throw new DBException(e.getMessage());
			
		} finally {
			Database.closeStatement(st);
			Database.closeResultSet(rs);
		}
		
	}
}
