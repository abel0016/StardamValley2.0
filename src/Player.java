import java.util.List;
import java.util.Map;

public class Player {
    private String name;
    private int nivel;
    private int exp;
    private int hp;
    private List<Map<Skills,Integer>> habilidades;

    public Player(String name, int nivel, int exp, int hp, List<Map<Skills,Integer>> habilidades) {
        this.name = name;
        this.nivel= nivel;
        this.exp = exp;
        this.hp = hp;
        this.habilidades = habilidades;
    }
    public void iniciarJugador(){
        habilidades.add(Map.of(Skills.REGAR,0,Skills.CULTIVAR,0)
        );
        setNivel(1);
        setExp(0);
        setHp(10);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public int getHp() {
        return hp;
    }

    public void setHp(int hp) {
        this.hp = hp;
    }

    public List<Map<Skills,Integer>> getHabilidades() {
        return habilidades;
    }

    public void setHabilidades(List<Map<Skills,Integer>> habilidades) {
        this.habilidades = habilidades;
    }

    public int getNivel() {
        return nivel;
    }

    public void setNivel(int nivel) {
        this.nivel = nivel;
    }
}
