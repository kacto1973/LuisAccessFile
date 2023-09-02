import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.Date;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RandomVendorFile {

    private String fileName;

    public RandomVendorFile(String fileName) {
        this.fileName = fileName;
    }



    public static long write(Vendor v) {
        RandomAccessFile out = null;
        long position = 0;
        byte buffer[] = null;

        try {
            out = new RandomAccessFile(fileName, "rws");

            // cuantos bytes hay en archivo
            position = out.length();

            // ir al ultimo byte
            out.seek(position);

            // escribir el codigo
            out.writeLong(v.getCodigo());

            // escribir los bytes que se requieren para imprimir
            // la cadena con el nombre
            buffer = v.getNombre().getBytes();
            out.write(buffer);

            // convertir de Date a long
            long dob = v.getFecha().getTime();
            out.writeLong(dob);

            // escribir los bytes que se requieren para imprimir
            // la cadena con la zona
            buffer = v.getZona().getBytes();
            out.write(buffer);

            //escribir el Long de ventas
            out.writeLong(v.getVentas());
            out.close();
        } catch (IOException ex) {
            Logger.getLogger(RandomVendorFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return position;
    }

    public static void addVendor(Vendor v) throws IOException {
        RandomAccessFile out = null;
        long position = 0;
        byte buffer[] = null;


        try {
            out = new RandomAccessFile(fileName, "rws");

            // cuantos bytes hay en archivo
            position = out.length();
            // ir al ultimo byte
            out.seek(position);
            // escribir el codigo
            out.writeLong(v.getCodigo());

            // escribir los bytes que se requieren para imprimir
            // la cadena con el nombre
            buffer = v.getNombre().getBytes();
            out.write(buffer);

            // convertir de Date a long
            long dob = v.getFecha().getTime();
            out.writeLong(dob);

            // escribir los bytes que se requieren para imprimir
            // la cadena con la zona
            buffer = v.getZona().getBytes();
            out.write(buffer);


            out.writeLong(v.getVentas());
            out.close();



        } catch (IOException ex) {
            Logger.getLogger(RandomVendorFile.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void borrarVendor(long position) throws Exception{

        RandomAccessFile out = new RandomAccessFile(fileName, "rws");
                byte[] emptyData = new byte[Vendor.RECORD_LEN];
                out.seek(position);
                out.write(emptyData); // Escribe datos vacíos sobre el registro que se desea eliminar
                out.close();
    }

    public Vendor read(long position) {
        RandomAccessFile out = null;
        long bytes = 0;
        byte buffer[] = null;
        Vendor vendor = null;
        try {
            out = new RandomAccessFile(fileName, "rws");
            out.seek(position);

            int codigo = out.readInt();

            byte[] nameBytes = new byte[Vendor.MAX_NAME];
            out.read(nameBytes);

            long dateBytes = out.readLong();

            byte[] zonaBytes = new byte[Vendor.MAX_ZONE];
            out.read(zonaBytes);
            Long ventas=out.readLong();

            vendor = new Vendor(codigo, new String(nameBytes), new Date(dateBytes),
                    new String(zonaBytes),ventas);
            out.close();

        } catch (IOException ex) {
            Logger.getLogger(RandomVendorFile.class.getName()).log(Level.SEVERE, null, ex);
        }
        return vendor;
    }

    public void read(Vendor vendors[]) {
        RandomAccessFile out = null;
        long bytes = 0;
        byte buffer[] = null;
        Vendor vendor = null;
        try {
            out = new RandomAccessFile(fileName, "rws");
            for (int i = 0; i < vendors.length; i++) {

                long codigo = out.readInt();

                byte[] nameBytes = new byte[Vendor.MAX_NAME];
                out.read(nameBytes);

                long dateBytes = out.readLong();

                byte[] zonaBytes = new byte[Vendor.MAX_ZONE];
                out.read(zonaBytes);
                Long ventas=out.readLong();

                vendors[i] = new Vendor((int) codigo, new String(nameBytes), new Date(dateBytes),
                        new String(zonaBytes),ventas);
            }
            out.close();

        } catch (IOException ex) {
            Logger.getLogger(RandomVendorFile.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    public static void modificarVendor(long pos, Vendor actVendor) {
        RandomAccessFile out = null;
        try {
            out = new RandomAccessFile(fileName, "rws");
            long fileLength = out.length();
            if (pos >= 0 && pos < fileLength) {
                out.seek(pos);
                out.skipBytes(4); // omite el hecho de que el campo ya se crea solo
                write(actVendor); // anota lo actualizado
                out.close();
            }
        } catch (Exception ex) {}
    }




    public static void main(String[] args) throws Exception {

        int opcion=0;
        try{
            //Scanner opcionador = new Scanner(System.in);
            System.out.println("Que quieres hacer?");
            System.out.println("1: Agregar nuevo vendero al archivo");
            System.out.println("2: Eliminar a un vendedor *necesario insertar posicion*");
            System.out.println("3: Modificar datos de un vendedor *menos el numero de empleado*");
            System.out.println("4: Realizar una consulta por zona *necesario ingresar zona por escrito*");
            System.out.println("5: Leer un registro en especifico *necesario ingresar posicion deseada*");
            opcion = Integer.parseInt(JOptionPane.showInputDialog("elige un numero del 1 al 5:"));
           // opcionador.close();
            //opcionador.
        }catch (Exception e){}

        switch (opcion){

            case 1:
            Vendor vendedor = new Vendor(0, JOptionPane.showInputDialog("Inserta el nombre:"),
                    JOptionPane.showInputDialog("Inserta la fecha: dd/mm/yyyy"),
                    JOptionPane.showInputDialog("Inserta zona/estado:"),
                    Long.parseLong(JOptionPane.showInputDialog("Inserte ventas:")));
            addVendor(vendedor);
                break;

            case 2:
            borrarVendor(Long.parseLong(JOptionPane.showInputDialog("que registro quieres eliminar?")));
                break;
            case 3:
                modificarVendor(
                        Long.parseLong(JOptionPane.showInputDialog("Que registro quieres modificar")),
                        new Vendor()
                );
                break;
            case 4:
                ///no pudimos..
                break;

            case 5://

                final String dataPath = "vendors-data.dat";

                RandomVendorFile randomFile = new RandomVendorFile(dataPath);

                Scanner input = new Scanner(System.in);

                System.out.println("Numero de registro que quieras leer:");

                int n = input.nextInt();

                int pos = (n * Vendor.RECORD_LEN) - Vendor.RECORD_LEN;

                //long t1 = System.currentTimeMillis();
                Vendor p = randomFile.read(pos);
                //long t2 = System.currentTimeMillis();
                System.out.println(p);
                //System.out.println(t2 - t1);
                break;
        }



    }//fin main

}
