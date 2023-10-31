package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import io.IO;
import model.Departamento;
import model.Empleado;

public class EmpresaSQL {
	private Connection conn = null;

	public EmpresaSQL() {
		conn = BD.getConnection();
		createTables();
	}

	public void close() {
		BD.close();
	}

	private void createTables() {
		String sqlEmpleado = "";
		String sqlDepartamento = "";
		String sqlEmpleado2 = "";

		if (BD.typeDB.equals("sqlite")) {
			sqlEmpleado = """
					       CREATE TABLE IF NOT EXISTS empleado (
					  		id INTEGER PRIMARY KEY AUTOINCREMENT,
					  		nombre TEXT NOT NULL,
					  		nacido TEXT,
					  		salario REAL,
					  		departamento_id INTEGER,
					FOREIGN KEY (departamento_id) REFERENCES departamento(id)
					       )
					   """;

			sqlDepartamento = """
					    CREATE TABLE IF NOT EXISTS departamento (
					        id INTEGER PRIMARY KEY AUTOINCREMENT,
					        nombre TEXT NOT NULL,
					        jefe_id INTEGER,
					        FOREIGN KEY (jefe_id) REFERENCES empleado(id)
					    )
					""";
		} else if (BD.typeDB.equals("mariadb")) {
			sqlEmpleado = """
					    CREATE TABLE IF NOT EXISTS empleado (
					        id INT PRIMARY KEY AUTO_INCREMENT,
					        nombre VARCHAR(255) NOT NULL,
					        nacido DATE,
					        salario DOUBLE,
					        departamento_id INT
					    )
					""";

			sqlDepartamento = """
					    CREATE TABLE IF NOT EXISTS departamento (
					        id INTEGER PRIMARY KEY AUTO_INCREMENT,
					        nombre VARCHAR(255) NOT NULL,
					        jefe_id INT,
					        FOREIGN KEY (jefe_id) REFERENCES empleado(id)
					    )
					""";

			sqlEmpleado2 = """
						ALTER TABLE departamento
							ADD FOREIGN KEY (jefe_id) REFERENCES empleado(id)
					""";
		}

		try {
			if (BD.typeDB.equals("sqlite")) {
				conn.createStatement().executeUpdate(sqlEmpleado);
				conn.createStatement().executeUpdate(sqlDepartamento);
			} else if (BD.typeDB.equals("mariadb")) {
				conn.createStatement().executeUpdate(sqlEmpleado);
				conn.createStatement().executeUpdate(sqlDepartamento);
				conn.createStatement().executeUpdate(sqlEmpleado2);
			}
		} catch (SQLException e) {
			IO.println(e.getMessage());
		}
	}

	public Departamento buscarDpto(int ide) {
		String sql = "";
		sql = """
				SELECT *
				FROM departamento
				WHERE id LIKE ?
				""";

		try {
			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, ide);
			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				return readDpto(rs);
			}
		} catch (SQLException e) {
		}
		return null;
	}

	public boolean deleteE(int id) {
		String sql = "";
		String sqlU = "";
		sql = """
				DELETE FROM empleado
				WHERE id = ?
				""";
		sqlU = """
				UPDATE departamento
				SET jefe_id=null
				WHERE jefe_id = ?
				""";

		try {
			PreparedStatement ps1 = conn.prepareStatement(sqlU);
			ps1.setInt(1, id);
			ps1.executeUpdate();

			PreparedStatement ps = conn.prepareStatement(sql);
			ps.setInt(1, id);

			return ps.executeUpdate() > 0;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;

	}

	public boolean deleteD(int id) {
		String sql = "";
		String sqlU = "";
		sql = """
				DELETE FROM departamento
				WHERE id = ?
				""";
		sqlU = """
				UPDATE empleado
				SET departamento_id=null
				WHERE departamento_id = ?""";
		if (BD.typeDB.equals("sqlite")) {

			try {
				PreparedStatement ps1 = conn.prepareStatement(sqlU);
				ps1.setInt(1, id);
				ps1.executeUpdate();
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setInt(1, id);

				return ps.executeUpdate() > 0;
			} catch (SQLException e) {
				e.printStackTrace();
			}
		} else if (BD.typeDB.equals("mariadb")) {

			try {
				PreparedStatement ps1 = conn.prepareStatement(sqlU);
				ps1.setInt(1, id);
				ps1.executeUpdate();

				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setInt(1, id);

				return ps.executeUpdate() > 0;
			} catch (SQLException e) {
				e.printStackTrace();
			}

		}
		return false;
	}

	public Empleado buscarEmpleado(int id) {
		String sql = "";
		sql = """
				SELECT *
				FROM empleado
				WHERE id LIKE ?
				""";
		if (BD.typeDB.equals("sqlite")) {

			try {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setInt(1, id);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					return read(rs);
				}
			} catch (Exception e) {

			}

		} else if (BD.typeDB.equals("mariadb")) {
			try {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setInt(1, id);
				ResultSet rs = ps.executeQuery();
				if (rs.next()) {
					return read(rs);
				}
			} catch (Exception e) {

			}

		}
		return null;
	}

	public boolean add(Empleado emp) {
		String sql = """
				INSERT INTO empleado (nombre, salario, nacido, departamento_id)
				VALUES (?, ?, ?, ?)
				""";

		try {
			if (BD.typeDB.equals("sqlite")) {

				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, emp.getNombre());
				ps.setString(2, Double.toString(emp.getSalario()));
				ps.setString(3, emp.getNacido().toString());
				if (emp.getDpto() == null) {
					ps.setInt(4, 0);
				} else {
					ps.setInt(4, emp.getDpto().getId());
				}
				return ps.executeUpdate() > 0;
			} else if (BD.typeDB.equals("mariadb")) {

				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, emp.getNombre());
				ps.setDouble(2, emp.getSalario());
				ps.setDate(3, Date.valueOf(emp.getNacido()));
				if (emp.getDpto() == null) {
					ps.setNull(4, Types.INTEGER);
				} else {
					ps.setInt(4, emp.getDpto().getId());
				}
				return ps.executeUpdate() > 0;
			}

		} catch (SQLException e) {
			IO.println(e.getMessage());
		}
		return false;
	}

	public boolean crearDepartamento(Departamento dep) {
		String sql = "";
		sql = """
				INSERT INTO departamento (nombre, jefe_id)
				VALUES (?, ?)
				""";
		if (BD.typeDB.equals("sqlite")) {
			try {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, dep.getNombre());
				if (dep.getJefe() == null) {
					ps.setInt(2, 0);
				} else {

					ps.setInt(2, dep.getJefe().getId());
				}
				return ps.executeUpdate() > 0;

			} catch (SQLException e) {
				e.getMessage();
			}
		} else if (BD.typeDB.equals("mariadb")) {
			try {
				PreparedStatement ps = conn.prepareStatement(sql);
				ps.setString(1, dep.getNombre());
				if (dep.getJefe() == null) {
					ps.setNull(2, Types.INTEGER);
				} else {

					ps.setInt(2, dep.getJefe().getId());
				}
				return ps.executeUpdate() > 0;

			} catch (Exception e) {
				IO.println(e.getMessage());
			}
		}
		return false;
	}

	public String showE() {
		String sql = """
				SELECT id, nombre, nacido, salario, departamento_id
				FROM empleado
				""";
		try {
			StringBuffer sb = new StringBuffer();
			ResultSet rs = conn.createStatement().executeQuery(sql);
			while (rs.next()) {
				Empleado e = read(rs);
				sb.append(e.toString());
				sb.append("\n");
			}
			return sb.toString();
		} catch (SQLException e) {
		}
		return "";
	}

	public String showD() {
		String sql = """
				SELECT id, nombre, jefe_id
				FROM departamento
				""";
		try {
			StringBuffer sb = new StringBuffer();
			ResultSet rs = conn.createStatement().executeQuery(sql);
			while (rs.next()) {
				Departamento d = readDpto(rs);
				sb.append(d.toString());
				sb.append("\n");
			}
			return sb.toString();
		} catch (SQLException e) {
		}
		return "";
	}

	private Departamento readDpto(ResultSet rs) {
		try {
			int id = rs.getInt("id");
			String nombre = rs.getString("nombre");
			int jefe_id = rs.getInt("jefe_id");
			Empleado jefe = buscarEmpleado(jefe_id);
			return new Departamento(id, nombre, jefe);
		} catch (Exception e) {

		}
		return null;
	}

	private Empleado read(ResultSet rs) {
		try {
			if (BD.typeDB.equals("sqlite")) {
				int id = rs.getInt("id");
				String nombre = rs.getString("nombre");
				double salario = Double.parseDouble(rs.getString("salario"));
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
				LocalDate nacido = LocalDate.parse(rs.getString("nacido"), formatter);
				int departamento_id = rs.getInt("departamento_id");
				Departamento dpto = buscarDpto(departamento_id);
				return new Empleado(id, nombre, salario, nacido, dpto);
			} else if (BD.typeDB.equals("mariadb")) {
				int id = rs.getInt("id");
				String nombre = rs.getString("nombre");
				double salario = rs.getDouble("salario");
				LocalDate nacido = rs.getDate("nacido").toLocalDate();
				int departamento_id = rs.getInt("departamento_id");
				Departamento dpto = buscarDpto(departamento_id);
				return new Empleado(id, nombre, salario, nacido, dpto);
			}

		} catch (SQLException e) {
		}
		return null;
	}
}
