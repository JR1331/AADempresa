package view;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import dao.EmpresaSQL;
import io.IO;
import model.Departamento;
import model.Empleado;

public class Menu {
	
	public static void main(String[] args) {
		EmpresaSQL empresa = new EmpresaSQL();
//		empresa.drop();
		
		List<String> opciones = List.of( 
				"1. Añadir departamento", 
				"2. Eliminar departamento", 
				"3. Mostrar departamentos", 
				"4. Añadir un empleado",
				"5. Eliminar un empleado",
				"6. Mostrar empleados",
				"7. Salir");
		
		while (true) {
			System.out.println(opciones);
			switch (IO.readInt()) {
			case 1:
				añadirDpto(empresa);
				break;
			case 2:
				eliminarDepartamento(empresa);
				break;
			case 3:
				mostrarD(empresa);
				break;
			case 4:
				anadirEmpleado(empresa);
				break;
			case 5:
				eliminarEmpleado(empresa);
				break;
			case 6:
				mostrarE(empresa);
				break;
			case 7:
				cerrarEmpresa(empresa);
				return;
			default:
			}
		}		
		
	}

	private static void mostrarD(EmpresaSQL empresa) {
		System.out.println(empresa.showD());
		
	}

	private static void añadirDpto(EmpresaSQL empresa) {
		boolean anadido=false;
		IO.print("Nombre ? ");
		String nombre = IO.readString();
		IO.print("Tiene jefe ? (Ingrese el id del jefe o 0 si no tiene): ");
		 int jefeId = IO.readInt();
		 if (jefeId!=0) {		
			Empleado emp = empresa.buscarEmpleado(jefeId);
			anadido = empresa.crearDepartamento(new Departamento(nombre, emp));
		}else {
		anadido = empresa.crearDepartamento(new Departamento(nombre));
		}
		 
		IO.println(anadido ? "Añadido" : "No se ha podido añadir");
		
	}

	private static void cerrarEmpresa(EmpresaSQL empresa) {
		empresa.close();
	}

	private static void eliminarEmpleado(EmpresaSQL empresa) {
		IO.print("Id ? ");
		int id = IO.readInt();
		
		boolean borrado = empresa.deleteE(id);
		IO.println(borrado ? "Borrado" : "No se ha podido borrar");
	}	
	
	private static void eliminarDepartamento(EmpresaSQL empresa) {
		IO.print("Id ? ");
		int id = IO.readInt();
		
		boolean borrado = empresa.deleteD(id);
		IO.println(borrado ? "Borrado" : "No se ha podido borrar");
	}

	private static boolean anadirEmpleado(EmpresaSQL empresa) {
		boolean anadido=false;
		IO.print("Nombre ? ");
		String nombre = IO.readString();
		IO.print("salario ? ");
		double salario = IO.readDouble();
		IO.print("Nacido ? ");
		String nacidoStr = IO.readString();
		LocalDate nacido = LocalDate.parse(nacidoStr, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
		 IO.print("Tienes departamento ? (Ingrese el número del departamento o 0 si no tiene): ");
		    int departamentoId = IO.readInt();
		    if (departamentoId==0) {
		        anadido = empresa.add(new Empleado(nombre, salario, nacido));
		    } else {
		        Departamento dep = empresa.buscarDpto(departamentoId);
		        anadido = empresa.add(new Empleado(nombre, salario, nacido, dep));
		    }
		    return anadido;
	}


	private static void mostrarE(EmpresaSQL empresa) {
		System.out.println(empresa.showE());
	}

}