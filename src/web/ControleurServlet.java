package web;

import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.connector.Response;

import dao.CategorieDaoImpl;
import dao.ICategorieDao;
import dao.IProduitDao;
import dao.ProduitDaoImpl;
import metier.entities.Categorie;
import metier.entities.Produit;
@WebServlet (name="cs",urlPatterns= {"/controleur","*.do"})
public class ControleurServlet extends HttpServlet {
	IProduitDao metier;
	ICategorieDao metierCat;

@Override
public void init() throws ServletException {
	metier = new ProduitDaoImpl();
	metierCat = new CategorieDaoImpl();
}
@Override
protected void doGet(HttpServletRequest request,
 HttpServletResponse response)
 throws ServletException, IOException {
String path=request.getServletPath();
if (path.equals("/index.do"))
{
request.getRequestDispatcher("produits.jsp").forward(request,response);
}
else if (path.equals("/chercher.do"))
{
String motCle=request.getParameter("motCle");
ProduitModele model= new ProduitModele();
model.setMotCle(motCle);
List<Produit> prods = metier.produitsParMC(motCle);
model.setProduits(prods);
request.setAttribute("model", model);
request.getRequestDispatcher("produits.jsp").forward(request,response);
}
else if (path.equals("/saisie.do") )
{
	List<Categorie> cats = metierCat.getAllCategories();
	CategorieModele model= new CategorieModele();
	model.setCategories(cats);
	request.setAttribute("catModel", model);
request.getRequestDispatcher("saisieProduit.jsp").forward(request,response);
}
else if (path.equals("/save.do") && request.getMethod().equals("POST"))
{
	String nom=request.getParameter("nom");
	Long categorieId=Long.parseLong(request.getParameter("categorie"));
	double prix = Double.parseDouble(request.getParameter("prix"));
	Categorie cat = metierCat.getCategorie(categorieId);
	Produit p = metier.save(new Produit(nom,prix,cat));
	request.setAttribute("produit", p);
	response.sendRedirect("chercher.do?motCle=");
}
else if (path.equals("/supprimer.do"))
{
 Long id= Long.parseLong(request.getParameter("id"));
 metier.deleteProduit(id);
 response.sendRedirect("chercher.do?motCle=");
}
else if (path.equals("/editer.do") )
{
	Long id= Long.parseLong(request.getParameter("id"));
	 Produit p = metier.getProduit(id);
	 request.setAttribute("produit", p);

	 List<Categorie> cats = metierCat.getAllCategories();
	 CategorieModele model= new CategorieModele();
	 model.setCategories(cats);
	 request.setAttribute("catModel", model);
request.getRequestDispatcher("editerProduit.jsp").forward(request,response);
}
else if (path.equals("/update.do") )
{
	 Long id = Long.parseLong(request.getParameter("id"));
	 String nom=request.getParameter("nom");
	 double prix = Double.parseDouble(request.getParameter("prix"));
	 Long categorieId=Long.parseLong(request.getParameter("categorie"));
	 Produit p = new Produit();
	 p.setIdProduit(id);
	 p.setNomProduit(nom);
	 p.setPrix(prix);
	 Categorie cat = metierCat.getCategorie(categorieId);
	 p.setCategorie(cat);
	 metier.updateProduit(p);
	 response.sendRedirect("chercher.do?motCle=");
}
else
{
response.sendError(Response.SC_NOT_FOUND);
}
}
@Override
protected void doPost(HttpServletRequest request,
 HttpServletResponse response) throws
ServletException, IOException {
doGet(request,response);
}
}
