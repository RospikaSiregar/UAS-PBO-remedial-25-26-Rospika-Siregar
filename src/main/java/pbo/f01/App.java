package pbo.f01;

import java.util.Scanner;
import java.util.List;
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

        while (scanner.hasNextLine()) {
            String input = scanner.nextLine();
            
            if (input == null || input.trim().isEmpty()) {
                continue;
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

                    // Validasi ketat pencegah NullPointerException
                    if (vehicle != null && area != null) {
                        // Menghitung jumlah kendaraan nyata di area parkir tersebut langsung dari database
                        Long currentCount = em.createQuery(
                            "SELECT COUNT(v) FROM Vehicle v WHERE v.parkingArea.name = :areaName", Long.class)
                            .setParameter("areaName", areaName)
                            .getSingleResult();

                        if (vehicle.getType() != null && vehicle.getType().equals(area.getAllowed_type())) {
                            if (currentCount < area.getCapacity()) {
                                vehicle.setParkingArea(area);
                                em.merge(vehicle);
                            }
                        }
                    }
                    em.getTransaction().commit();

                } else if (command.equals("display-all")) {
                    // Mengambil seluruh area parkir, diurutkan Ascending secara alfabetis (A-Z)
                    List<Parkir> areas = em.createQuery("SELECT p FROM Parkir p ORDER BY p.name ASC", Parkir.class).getResultList();
                    
                    for (Parkir a : areas) {
                        // Mengambil daftar kendaraan nyata yang parkir di area ini, diurutkan Ascending berdasarkan nomor plat
                        List<Vehicle> vehiclesInArea = em.createQuery(
                            "SELECT v FROM Vehicle v WHERE v.parkingArea.name = :areaName ORDER BY v.plate_number ASC", Vehicle.class)
                            .setParameter("areaName", a.getName())
                            .getResultList();

                        // Cetak nama area dengan format: nama allowed_type capacity|jumlah_terisi
                        System.out.println(a.getName() + " " + a.getAllowed_type() + " " + a.getCapacity() + "|" + vehiclesInArea.size());
                        
                        // Cetak daftar kendaraan yang ada di dalam area tersebut
                        for (Vehicle v : vehiclesInArea) {
                            System.out.println(v.toString());
                        }
                    }
                }
            } catch (Exception e) {
                if (em.getTransaction().isActive()) {
                    em.getTransaction().rollback();
                }
            }
        }

        scanner.close();
        em.close();
        emf.close();
    }
}