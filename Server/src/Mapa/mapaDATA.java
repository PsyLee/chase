package Mapa;

import java.util.ArrayList;

public class mapaDATA { // ovoj treba da se prati preko
												// net

	public int golemina_x;
	public int golemina_y;

	public ArrayList<Koordinati> lista_objekti = new ArrayList<Koordinati>();
	public ArrayList<Koordinati> igraci_pozicii = new ArrayList<Koordinati>();

	public int brojPozicii() {
		return igraci_pozicii.size();
	}

	public int brojObjekti() {
		return lista_objekti.size();
	}

	public mapaDATA(int golemina_x, int golemina_y,
			ArrayList<Koordinati> igraci, ArrayList<Koordinati> objekti) {
		this.golemina_x = golemina_x;
		this.golemina_y = golemina_y;
		this.lista_objekti = objekti;
		this.igraci_pozicii = igraci;
	}
	public mapaDATA(){}
}