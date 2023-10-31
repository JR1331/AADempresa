package model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Departamento {
	
	public int id;
	public String nombre;
	public Empleado jefe;
	
	
	public Departamento(String nombre, Empleado jefe) {
		id=0;
		this.nombre=nombre;
		this.jefe=jefe;
	}
	public Departamento(String nombre) {
		id=0;
		this.nombre=nombre;
		jefe=null;
	}

}

