package Mapa;

import java.util.ArrayList;
import java.util.Random;

public class Mapa {

	private int broj_objekti = 0;

	private int[][] mapa;
	public ArrayList<Koordinati> lista_objekti = new ArrayList<Koordinati>();
	public ArrayList<Koordinati> igraci_pozicii = new ArrayList<Koordinati>();
	public mapaDATA data;

	public Mapa(int x, int y, int broj_na_objekti) {
		mapa = new int[x][y];
		this.broj_objekti = broj_na_objekti;
	}
	public Mapa(){}

	public void generiraj() {
		int x_size = mapa.length;
		int y_size = mapa[0].length;

		Random r = new Random();

		for (int i = 0; i < 30; i++) { // 10 random polozbi za igraci
			int lokacija_x = r.nextInt(mapa.length);
			int lokacija_y = r.nextInt(mapa[0].length);
			mapa[lokacija_x][lokacija_y] = 9;
			igraci_pozicii.add(new Koordinati(lokacija_x, lokacija_y));
		}

		for (Koordinati k : igraci_pozicii) { // ima barem 1 pat medju sekoj
												// igrac
			int pocetna_x = k.x;
			int pocetna_y = k.z;

			// String krajni_koord[] = igraci_pozicii.get(i + 1).split(" ");
			int krajna_x = igraci_pozicii.get(igraci_pozicii.size() - 1).x;
			int krajna_y = igraci_pozicii.get(igraci_pozicii.size() - 1).z;

			while (pocetna_x != krajna_x || pocetna_y != krajna_y) {
				Random ran = new Random();
				int random = ran.nextInt(2);

				if (random == 0 && pocetna_x != krajna_x) {
					if (pocetna_x < krajna_x) {
						pocetna_x++;
					} else {
						pocetna_x--;
					}
					mapa[pocetna_x][pocetna_y] = 2;
				}
				if (random == 1 && pocetna_y != krajna_y) {
					if (pocetna_y < krajna_y) {
						pocetna_y++;
					} else {
						pocetna_y--;
					}
					mapa[pocetna_x][pocetna_y] = 2;
				}
			}
		}

		for (int i = 0; i < broj_objekti; i++) {
			int proverki = 0;

			while (proverki < mapa.length * mapa[0].length) {
				int lokacija_x = r.nextInt(mapa.length);
				int lokacija_y = r.nextInt(mapa[0].length);

				if (mapa[lokacija_x][lokacija_y] == 0) {
					mapa[lokacija_x][lokacija_y] = 1;
					lista_objekti.add(new Koordinati(lokacija_x, lokacija_y));
					break;
				}
				proverki++;
			}

		}
                poziciite();
		data = new mapaDATA(x_size, y_size, igraci_pozicii, lista_objekti);
		// pecati_matrica();
		// pecati_objekti();
	}

	public mapaDATA sendData() { // ovoj treba da se povika kd se prakja mapa
		return data;
	}
        
        public void poziciite(){
            igraci_pozicii.clear();
            for(int i=0;i<mapa.length;i++){
                for(int j=0;j<mapa[0].length;j++){
                    if(mapa[i][j] == 9){
                        igraci_pozicii.add(new Koordinati(i, j));
                    }
                }
            }
        }
        
        

	public void pecati_matrica() {
		for (int i = 0; i < mapa.length; i++) {
			for (int j = 0; j < mapa[0].length; j++) {
				System.out.print(" " + mapa[i][j]);
			}
			System.out.println("");
		}
	}

}
