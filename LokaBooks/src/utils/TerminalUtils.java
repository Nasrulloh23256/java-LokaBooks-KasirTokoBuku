package utils;

/**
 * @deprecated This class uses system-dependent commands and may not work on all platforms.
 * Consider using a platform-independent solution in the future.
 */
@Deprecated
public class TerminalUtils {
    /**
     * @deprecated This method uses system-dependent commands and may not work consistently across different terminals.
     * Consider using a platform-independent solution or a dedicated terminal library.
     */
    @Deprecated
    public static void clearScreen() {
        try {
            String operatingSystem = System.getProperty("os.name").toLowerCase();

            if (operatingSystem.contains("windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J");
                System.out.flush();
            }
        } catch (Exception e) {
            // Jika gagal, gunakan metode alternatif
            for (int i = 0; i < 50; i++) {
                System.out.println();
            }
        }
    }
} 