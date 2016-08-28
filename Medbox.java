
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;

/**
 * 
 * @author Kumara Ravi kanth Eragapati
 * @version 0.0.1
 * 
 */
public class Medbox {
	/*
	 * Enable SPI interface Before you start run-ing this program you have to
	 * check if the SPI interface of the Raspberry Pi is enabled to check enter
	 * this command in terminal "lsmod" check for spi_bcm2708 or spi_bcm2835 is
	 * listed in, if exist it mean SPI is enabled if not enabled, you can do it
	 * by going to Menu -> preferences -> Raspberry Pi configuration ->
	 * "interfaces tab"
	 */

	/*
	 * Error : WiringPiSetup: Must be root. (Did you forget sudo?) If you seeing
	 * this error when you execute the program it means you didnt open the IDE
	 * application as sudo user. I am using eclipse and this is how to open
	 * eclipse as sudo user open the terminal and enter the following command
	 * "sudo eclipse &" wait for a while some times this might take little
	 * longer to open if it didnt work reboot the system and try again
	 * 
	 * you can always write your code extract it as a jar file and try executing
	 * it as a sudo user. like "sudo su" this will ask you for your password,
	 * "java -jar MedBox.jar"
	 */

	/*
	 * Can't find the files in workspace, after reboot if opened eclipse as a
	 * root user as mentioned above your work directory will be /root/workspace
	 * and when you open it as normal user, your work directory is different.
	 * even if you try to change your work directory to /root/workspace in the
	 * normally opened eclipse, you wont see any such directory in the root...
	 */

	/*
	 * Software SPI pin connection between MCP3008 and Raspberry pi
	 * 
	 * I am using Raspberry Pi 3 Model B with 40 pins
	 * 					+---------Connections-------+
	 *  				+	MCP3008  ->   PI 3		+
	 *	     			+----------- -> ------------+ 
	 * 					+    VDD	 ->    3.3VDC	+
	 * 					+	 VREF	 ->    3.3VDC 	+
	 * 					+	 AGND	 ->     GND		+
	 * 					+	 CLK	 ->     SCLK	+
	 * 					+	 DOUT	 ->     MISO	+
	 * 					+	 DIN 	 ->     MISI	+
	 * 					+	 CS/SHDN ->     CE0		+
	 * 					+	 DGND	 ->     GND     +
	 * 					+---------Connections-------+
	 *  				+	MCP3008  ->   PI 3		+
	 *	     			+----------- -> ------------+
	 *
	 * Its not necessary to connect MCP3008 in this exact way there are many
	 * other way , like Hardware SPI, and in fact you can connect CLK DOUT DIN &
	 * CS/SHDN to any GPIO pins the only think to remember is to change few
	 * lines of code below, that states what pins are connected to MCP3008
	 * you can get the below information by executing following command in terminal - gpio readall 
	 *
	 *  +----------------------+-----+---------+-RaspBerry PI 3 model B-+---------+-----+----------------------+
  	 *	| pi4j.io.gpio.RaspiPin| wPi |  Name   | Mode | Physical | Mode | Name    | wPi | pi4j.io.gpio.RaspiPin|
 	 *	+----------------------+-----+----+----+------+----++----+------+---------+-----+----------------------+
 	 *	|					   |     |    3.3v |      |  1 || 2  |      | 5v      |     |					   |
 	 *	|	RaspiPin.GPIO_08   |   8 |   SDA.1 |   IN |  3 || 4  |      | 5V      |     |					   |
 	 *	|	RaspiPin.GPIO_09   |   9 |   SCL.1 |   IN |  5 || 6  |      | 0v      |     |					   |
 	 *	|	RaspiPin.GPIO_07   |   7 | GPIO. 7 |   IN |  7 || 8  | IN   | TxD     | 15  |	RaspiPin.GPIO_15   |
 	 *	|					   |     |      0v |      |  9 || 10 | IN   | RxD     | 16  |	RaspiPin.GPIO_16   |
 	 *	|	RaspiPin.GPIO_00   |   0 | GPIO. 0 |   IN | 11 || 12 | OUT  | GPIO. 1 | 1   |	RaspiPin.GPIO_01   |
 	 *	|	RaspiPin.GPIO_02   |   2 | GPIO. 2 |   IN | 13 || 14 |      | 0v      |     |					   |
 	 *	|	RaspiPin.GPIO_03   |   3 | GPIO. 3 |   IN | 15 || 16 | IN   | GPIO. 4 | 4   |	RaspiPin.GPIO_04   |
 	 *	|					   |     |    3.3v |      | 17 || 18 | OUT  | GPIO. 5 | 5   |	RaspiPin.GPIO_05   |
 	 *	|	RaspiPin.GPIO_12   |  12 |    MOSI |  OUT | 19 || 20 |      | 0v      |     |					   |
 	 *	|	RaspiPin.GPIO_13   |  13 |    MISO |   IN | 21 || 22 | OUT  | GPIO. 6 | 6   |	RaspiPin.GPIO_06   |
 	 *	|	RaspiPin.GPIO_14   |  14 |    SCLK |  OUT | 23 || 24 | OUT  | CE0     | 10  |	RaspiPin.GPIO_10   |
 	 *	|	      			   |     |      0v |      | 25 || 26 | IN   | CE1     | 11  |	RaspiPin.GPIO_11   |
 	 *	|	RaspiPin.GPIO_30   |  30 |   SDA.0 |   IN | 27 || 28 | IN   | SCL.0   | 31  |	RaspiPin.GPIO_31   |
 	 *	|	RaspiPin.GPIO_21   |  21 | GPIO.21 |   IN | 29 || 30 |      | 0v      |     |					   |
 	 *	|	RaspiPin.GPIO_22   |  22 | GPIO.22 |   IN | 31 || 32 | IN   | GPIO.26 | 26  |	RaspiPin.GPIO_26   |
 	 *	|	RaspiPin.GPIO_23   |  23 | GPIO.23 |   IN | 33 || 34 |      | 0v      |     |					   |
 	 *	|	RaspiPin.GPIO_24   |  24 | GPIO.24 |   IN | 35 || 36 | IN   | GPIO.27 | 27  |	RaspiPin.GPIO_27   |
 	 *	|	RaspiPin.GPIO_25   |  25 | GPIO.25 |   IN | 37 || 38 | IN   | GPIO.28 | 28  |	RaspiPin.GPIO_28   |
 	 *	|					   |     |      0v |      | 39 || 40 | IN   | GPIO.29 | 29  |	RaspiPin.GPIO_29   |
	 *	+----------------------+-----+---------+------+----++----+------+---------+-----+----------------------+
 	 *	| pi4j.io.gpio.RaspiPin| wPi |   Name  | Mode | Physical | Mode | Name    | wPi | pi4j.io.gpio.RaspiPin|
 	 *	+----------------------+-----+---------+-RaspBerry PI 3 model B-+---------+-----+----------------------+
 	 *
	 */
	private static Pin spiClk = RaspiPin.GPIO_14; // Phy Pin no #23, SCLK
	private static Pin spiMiso = RaspiPin.GPIO_13; // Phy Pin no #21, data in.
													// MISO: //
													// Master In Slave Out
	private static Pin spiMosi = RaspiPin.GPIO_12; // Phy Pin no #19, data out.
													// MOSI:
													// Master Out Slave In
	private static Pin spiCs = RaspiPin.GPIO_10; // Phy Pin no #24, Chip Select

	private static GpioPinDigitalInput misoInput = null;
	private static GpioPinDigitalOutput mosiOutput = null;
	private static GpioPinDigitalOutput clockOutput = null;
	private static GpioPinDigitalOutput chipSelectOutput = null;

	private static boolean go = true;

	public static void main(String[] args) {

		GpioController gpio = GpioFactory.getInstance();
		mosiOutput = gpio.provisionDigitalOutputPin(spiMosi, "MOSI",
				PinState.LOW);
		clockOutput = gpio.provisionDigitalOutputPin(spiClk, "CLK",
				PinState.LOW);
		chipSelectOutput = gpio.provisionDigitalOutputPin(spiCs, "CS",
				PinState.LOW);

		misoInput = gpio.provisionDigitalInputPin(spiMiso, "MISO");

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				System.out.println("Shutting down.");
				go = false;
			}
		});
		System.out.println("s.no\t|\t0|\t1|\t2|\t3|\t4|\t5|\t6|\t7|");
		int count = 0;
		while (go) {
			int[] value = {0, 0, 0, 0, 0, 0, 0, 0};
			//System.out.println("|\t|\t1|\t2|\t3|\t4|\t5|\t6|\t7|");
			for (int i = 0; i < 8; i++) {
				int adc = readADC(i);
				value[i] = adc;
			}
			System.out.println(count+"\t|\t"+value[0]+"|\t"+value[1]+"|\t"+value[2]+"|\t"+value[3]+"|\t"+value[4]+"|\t"+value[5]+"|\t"+value[6]+"|\t"+value[7]+"|");

			try {
				Thread.sleep(100L);
			} catch (InterruptedException ie) {
				ie.printStackTrace();
			}count++;
		}
		System.out.println("Bye...");
		gpio.shutdown();

	}

	public static int readADC(int mcp_Channel_No) {
		chipSelectOutput.high();

		clockOutput.low();
		chipSelectOutput.low();

		int adccommand = mcp_Channel_No;
		adccommand |= 0x18; // 0x18: 00011000
		adccommand <<= 3;
		// Send 5 bits: 8 - 3. 8 input channels on the MCP3008.
		for (int i = 0; i < 5; i++) //
		{
			if ((adccommand & 0x80) != 0x0) // 0x80 = 0&10000000
				mosiOutput.high();
			else
				mosiOutput.low();
			adccommand <<= 1;
			clockOutput.high();
			clockOutput.low();
		}

		int adcOut = 0;
		for (int i = 0; i < 12; i++) // Read in one empty bit, one null bit and
										// 10 ADC bits
		{
			clockOutput.high();
			clockOutput.low();
			adcOut <<= 1;

			if (misoInput.isHigh()) {
				adcOut |= 0x1;
			}
		}
		chipSelectOutput.high();

		adcOut >>= 1; // Drop first bit
		return adcOut;
	}

}
