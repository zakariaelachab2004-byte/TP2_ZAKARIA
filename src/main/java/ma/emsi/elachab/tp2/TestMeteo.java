package ma.emsi.elachab.tp2;

public class TestMeteo {
    public static void main(String[] args) {
        MeteoTool meteoTool = new MeteoTool();

        // Test 1 : Prévision pluie Paris (48.85, 2.35) pour les 3 prochains jours
        System.out.println("=== Test direct MeteoTool ===");
        boolean pluieParis = meteoTool.isPluiePrevue(48.85, 2.35, 0, 3);
        System.out.println("Pluie prévue à Paris (3 jours) : " + pluieParis);

        // Test 2 : Géolocalisation de Paris (utilisation de l'objet Coordonnees)
        MeteoTool.Coordonnees coords = meteoTool.getCoordonnees("Paris");
        System.out.println("Coordonnées de Paris : Lat = " + coords.latitude + ", Lon = " + coords.longitude);
    }
}