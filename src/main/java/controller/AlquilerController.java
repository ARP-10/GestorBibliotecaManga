package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import dao.AlquilerDAO;
import model.Alquiler;
import util.DB_Connection;

@WebServlet("/alquileres")
public class AlquilerController extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private AlquilerDAO alquilerDAO;
	
	@Override
	public void init() throws ServletException {
		// Se inicializa la conexión a la BBDD
		try {
			DB_Connection dbConnection = new DB_Connection();
			Connection conn = dbConnection.obtenerConexion();
			alquilerDAO = new AlquilerDAO(conn);
			System.out.println("Conexión a la base de datos inicializada correctamente.");
		} catch (SQLException | ClassNotFoundException e) {
			throw new ServletException("ERROR al inicializar la conexión a la BBDD", e);
		}
	}


       
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		try {
			System.out.println("Obteniendo alquileres...");
			// Obtener todos los capítulos
			Iterable<Alquiler> alquileres = alquilerDAO.obtenerTodosCrud();
			System.out.println("Alquileres obtenidos: " + (alquileres != null ? "sí" : "no"));
			request.setAttribute("alquileres", alquileres);
			request.getRequestDispatcher("/WEB-INF/view/AlquileresView.jsp").forward(request,  response);
		} catch (SQLException e) {
			System.err.println("Error al obtener alquileres: " + e.getMessage());
			e.printStackTrace();
			throw new ServletException("Error al obtener alquileres", e);
		}
	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// Dónde cambiemos action por otro parámetro (crear, actualizar eliminar) en el .jsp entrará por aquí
		String action = request.getParameter("action");
		// TODO: Ajustar el actualizar a que solo cambie la fecha fin
		try {
			if ("crear".equals(action)) {
			    String fechaInicioStr = request.getParameter("fecha_inicio");
			    String fechaFinStr = request.getParameter("fecha_fin");
			    Date fechaInicio = Date.valueOf(fechaInicioStr); 
			    Date fechaFin = Date.valueOf(fechaFinStr);
			    boolean alquilado = Boolean.parseBoolean(request.getParameter("alquilado"));
			    String dni_usuario = request.getParameter("dni_usuario");
			    int idManga = Integer.parseInt(request.getParameter("id_manga")); 
			    String titulo = request.getParameter("titulo");

			    Alquiler nuevoAlquiler = new Alquiler(0, fechaInicio, fechaFin, alquilado, dni_usuario, idManga, titulo);
			    alquilerDAO.crearNuevoCrud(nuevoAlquiler);

			}  else if ("actualizar".equals(action)) {
			    int id = Integer.parseInt(request.getParameter("id")); // ID del alquiler
			    String fechaInicioStr = request.getParameter("fecha_inicio");
			    String fechaFinStr = request.getParameter("fecha_fin");
			    Date fechaInicio = Date.valueOf(fechaInicioStr); 
			    Date fechaFin = Date.valueOf(fechaFinStr);
			    boolean alquilado = Boolean.parseBoolean(request.getParameter("alquilado"));
			    String dni_usuario = request.getParameter("dni_usuario");
			    int idManga = Integer.parseInt(request.getParameter("id_manga")); // Obtén el id_manga
			    String titulo = request.getParameter("titulo");

			    // Construye el objeto Alquiler con el id_manga incluido
			    Alquiler alquilerActualizado = new Alquiler(id, fechaInicio, fechaFin, alquilado, dni_usuario, idManga, titulo);
			    alquilerDAO.actualizarCrud(alquilerActualizado);

				
			}  else if ("eliminar".equals(action)) {
				int id = Integer.parseInt(request.getParameter("id"));
				alquilerDAO.borrarCrud(id);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
