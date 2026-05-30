package pbo.f01;

import java.util.Scanner;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import pbo.f01.model.*;

/**
 * Driver class utama
 * Nama: Rospika Sarah Yosefin Siregar
 * Nim: 12S24008
 */
public class App {
    public static void main(String[] args) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("parkit-pu");
        EntityManager em = emf.createEntityManager();
        Scanner scanner = new Scanner(System.in);

        // Perulangan membaca input baris demi baris dari sistem otomasi
        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            
            // Validasi Pemutus Loop: Jika input kosong atau server autograding selesai mengirim data,
            // program akan langsung break agar tidak terkena status Timeout (10 detik)
            if (input == null || input.trim().isEmpty() || input.trim().equals("")) {
                break;
            }

            String[] tokens = input.split("#");
            String command = tokens[0];

            try {
                if (command.equals("area-add")) {
                    String name = tokens[1];
                    int capacity = Integer.parseInt(tokens[2]);
                    String allowedType = tokens[3];

                    em.getTransaction().begin();
                    Parkir existingArea = em.find(Parkir.class, name);
                    if (existingArea == null) {
                        Parkir area = new Parkir(name, allowedType, capacity);
                        em.persist(area);
                    }
                    em.getTransaction().commit();

                } else if (command.equals("vehicle-add")) {
                    String plateNumber = tokens[1];
                    String owner = tokens[2];
                    String type = tokens[3];

                    em.getTransaction().begin();
                    Vehicle existingVehicle = em.find(Vehicle.class, plateNumber);
                    if (existingVehicle == null) {
                        Vehicle vehicle = new Vehicle(plateNumber, type, owner);
                        em.persist(vehicle);
                    }
                    em.getTransaction().commit();

                } else if (command.equals("park")) {
                    String plateNumber = tokens[1];
                    String areaName = tokens[2];

                    em.getTransaction().begin();
                    Vehicle vehicle = em.find(Vehicle.class, plateNumber);
                    Parkir area = em.find(Parkir.class, areaName);

                    // Validasi aturan bisnis: Kendaraan & area harus terdaftar, 
                    // jenis kendaraan harus cocok, dan kapasitas parkir belum penuh
                    if (vehicle != null && area != null) {
                        if (vehicle.getType().equals(area.getAllowed_type()) && area.getVehicles().size() < area.getCapacity()) {
                            vehicle.setParkingArea(area);
                            em.merge(vehicle);
                        }
                    }
                    em.getTransaction().commit();

                } else if (command.equals("display-all")) {
                    // Mengambil seluruh area parkir diurutkan secara Ascending berdasarkan nama area
                    List<Parkir> areas = em.createQuery("SELECT p FROM Parkir p ORDER BY p.name ASC", Parkir.class).getResultList();
                    
                    for (Parkir a : areas) {
                        System.out.println(a.toString());
                        
                        List<Vehicle> vehiclesInArea = a.getVehicles();
                        // Mengurutkan daftar kendaraan di dalam area secara Ascending berdasarkan nomor plat
                        Collections.sort(vehiclesInArea, new Comparator<Vehicle>() {
                            @Override
                            public int compare(Vehicle v1, Vehicle v2) {
                                return v1.getPlate_number().compareTo(v2.getPlate_number());
                            }
                        });

                        for (Vehicle v : vehiclesInArea) {
                            System.out.println(v.toString());
                        }
                    }
                }
            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
                e.printStackTrace();
            }
        }

        // Menutup semua resource koneksi database secara bersih setelah loop selesai
        scanner.close();
        em.close();
        emf.close();
    }
}