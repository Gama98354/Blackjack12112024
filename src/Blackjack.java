import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;

class Carta {
    private String palo;
    private String valor;

    public Carta(String palo, String valor) {
        this.palo = palo;
        this.valor = valor;
    }

    public String getValor() {
        return valor;
    }

    public int getValorNumerico() {
        switch (valor) {
            case "A":
                return 11; // El valor inicial del As es 11, se ajustará después si es necesario.
            case "K": case "Q": case "J":
                return 10;
            default:
                return Integer.parseInt(valor);
        }
    }

    @Override
    public String toString() {
        return valor + " de " + palo;
    }
}

class Baraja {
    private ArrayList<Carta> cartas;

    public Baraja() {
        cartas = new ArrayList<>();
        String[] palos = {"Corazones", "Diamantes", "Tréboles", "Picas"};
        String[] valores = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"};

        for (String palo : palos) {
            for (String valor : valores) {
                cartas.add(new Carta(palo, valor));
            }
        }

        Collections.shuffle(cartas);
    }

    public Carta repartirCarta() {
        return cartas.remove(cartas.size() - 1);
    }
}

class Jugador {
    private ArrayList<Carta> mano;
    private int puntuacion;
    private double saldo;
    private double apuesta;

    public Jugador(double saldoInicial) {
        mano = new ArrayList<>();
        puntuacion = 0;
        saldo = saldoInicial;
        apuesta = 0;
    }

    public void realizarApuesta(double cantidad) {
        if (cantidad <= saldo) {
            apuesta = cantidad;
            saldo -= cantidad;
        } else {
            System.out.println("Saldo insuficiente para apostar esta cantidad.");
        }
    }

    public void ganarApuesta() {
        saldo += apuesta * 2;
        apuesta = 0;
    }

    public void empatarApuesta() {
        saldo += apuesta;
        apuesta = 0;
    }

    public void recibirCarta(Carta carta) {
        mano.add(carta);
        puntuacion += carta.getValorNumerico();
        ajustarPorAses();
    }

    public int getPuntuacion() {
        return puntuacion;
    }

    public double getSaldo() {
        return saldo;
    }

    public ArrayList<Carta> getMano() {
        return mano;
    }

    public void limpiarMano() {
        mano.clear();
        puntuacion = 0;
        apuesta = 0;
    }

    private void ajustarPorAses() {
        int numAses = 0;
        for (Carta carta : mano) {
            if (carta.getValor().equals("A")) {
                numAses++;
            }
        }
        while (puntuacion > 21 && numAses > 0) {
            puntuacion -= 10;
            numAses--;
        }
    }

    @Override
    public String toString() {
        return "Mano: " + mano + " | Puntuación: " + puntuacion;
    }
}

public class Blackjack {
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.print("Ingresa el saldo inicial del jugador: ");
        double saldoInicial = scanner.nextDouble();
        Jugador jugador = new Jugador(saldoInicial);
        Jugador dealer = new Jugador(0);  // El dealer no tiene saldo ni apuesta.

        while (true) {
            Baraja baraja = new Baraja();
            System.out.print("Tu saldo actual es: $" + jugador.getSaldo() + "\n¿Cuánto deseas apostar? ");
            double apuesta = scanner.nextDouble();
            jugador.realizarApuesta(apuesta);

            // Repartir cartas iniciales
            jugador.recibirCarta(baraja.repartirCarta());
            jugador.recibirCarta(baraja.repartirCarta());
            dealer.recibirCarta(baraja.repartirCarta());
            dealer.recibirCarta(baraja.repartirCarta());

            System.out.println("\nTus cartas: " + jugador.getMano() + " | Puntuación: " + jugador.getPuntuacion());
            System.out.println("Carta visible del dealer: " + dealer.getMano().get(0));

            // Turno del jugador
            while (jugador.getPuntuacion() < 21) {
                System.out.print("¿Quieres 'hit' (pedir carta) o 'stand' (plantarte)? ");
                String opcion = scanner.next();

                if (opcion.equalsIgnoreCase("hit")) {
                    jugador.recibirCarta(baraja.repartirCarta());
                    System.out.println("Tus cartas: " + jugador.getMano() + " | Puntuación: " + jugador.getPuntuacion());
                    if (jugador.getPuntuacion() > 21) {
                        System.out.println("Te has pasado. ¡Pierdes esta ronda!");
                        break;
                    }
                } else if (opcion.equalsIgnoreCase("stand")) {
                    break;
                } else {
                    System.out.println("Opción no válida.");
                }
            }

            // Turno del dealer (si el jugador no se pasó)
            if (jugador.getPuntuacion() <= 21) {
                System.out.println("\nCartas del dealer: " + dealer.getMano() + " | Puntuación: " + dealer.getPuntuacion());
                while (dealer.getPuntuacion() < 17) {
                    dealer.recibirCarta(baraja.repartirCarta());
                    System.out.println("Cartas del dealer: " + dealer.getMano() + " | Puntuación: " + dealer.getPuntuacion());
                }
            }

            // Determinar el ganador
            if (jugador.getPuntuacion() > 21) {
                System.out.println("Perdiste la apuesta de $" + apuesta);
            } else if (dealer.getPuntuacion() > 21 || jugador.getPuntuacion() > dealer.getPuntuacion()) {
                System.out.println("¡Ganaste! Has ganado $" + apuesta);
                jugador.ganarApuesta();
            } else if (jugador.getPuntuacion() == dealer.getPuntuacion()) {
                System.out.println("Es un empate. Recuperas tu apuesta.");
                jugador.empatarApuesta();
            } else {
                System.out.println("El dealer gana. Pierdes tu apuesta de $" + apuesta);
            }

            System.out.println("Tu saldo actual es: $" + jugador.getSaldo());

            // Limpiar manos
            jugador.limpiarMano();
            dealer.limpiarMano();

            // Verificar si el jugador desea seguir jugando
            System.out.print("\n¿Quieres jugar otra ronda? (sí/no): ");
            String respuesta = scanner.next();
            if (!respuesta.equalsIgnoreCase("sí")) {
                System.out.println("Gracias por jugar. Tu saldo final es: $" + jugador.getSaldo());
                break;
            }
        }

        scanner.close();
    }
}