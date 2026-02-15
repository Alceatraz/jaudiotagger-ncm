import org.junit.jupiter.api.Test;

public class BitTest {

  byte[] data = {0, 0, 0, 34};

  @Test
  public void testBit() {

    int byte1 = data[0] & 0xFF;

    boolean last = (byte1 >>> 7) == 1;

    int type = byte1 & 0b01111111;

    int size1 = data[1] & 0xFF;
    int size2 = data[2] & 0xFF;
    int size3 = data[3] & 0xFF;

    System.out.println(last);
    System.out.println(type);

    System.out.println(Integer.toBinaryString(size1));
    System.out.println(Integer.toBinaryString(size2));
    System.out.println(Integer.toBinaryString(size3));

    int size = (size1 << 16) | (size2 << 8) | size3;

    System.out.println(Integer.toBinaryString(size));

    System.out.println(0b00000000_00000000_00000000_00000000);
    System.out.println(0b10000000_00000000_00000000_00000000);
    System.out.println(0b01111111_11111111_11111111_11111111);

  }

}
