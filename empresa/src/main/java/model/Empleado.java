package model;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Empleado {

	public int id;
	public String nombre;
	public double salario;
	public LocalDate nacido;
	public Departamento dpto;
	
	public Empleado(String nombre, double salario, LocalDate nacido) {
		this.nombre = nombre;
		this.salario = salario;
		this.nacido = nacido;
		dpto=null;
	}

	public Empleado(String nombre, double salario, LocalDate nacido, Departamento dpto) {
		this.nombre = nombre;
		this.salario = salario;
		this.nacido = nacido;
		this.dpto = dpto;
	}

	
}

