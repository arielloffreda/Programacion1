/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.logging.Level;
import java.util.logging.Logger;
import persona.Alumno;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;
import persona.MiCalendario;
import persona.MiCalendarioException;
import persona.PersonaException;

/**
 *
 * @author gguzm
 */
public class AlumnoDAOTXT extends DAO<Alumno, Long>{

        private RandomAccessFile raf;
    
    public AlumnoDAOTXT(String filename) throws DAOException {
        try {
            raf = new RandomAccessFile(filename, "rws");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(AlumnoDAOTXT.class.getName()).log(Level.SEVERE, null, ex);
            throw new DAOException("Error al crear el DAO ==> "+ex.getMessage());
        }
    }
    
    @Override
    public void create(Alumno alumno) throws DAOException{
        
        try {
            if (existe(alumno.getDni())) {
                throw new DAOException("El alumno ya existe");
            }
            
            raf.seek(raf.length()); // Va al final del archivo
            raf.writeBytes(alumno.toString()+System.lineSeparator());
            
        } catch (IOException ex) {
            Logger.getLogger(AlumnoDAOTXT.class.getName()).log(Level.SEVERE, null, ex);
            throw new DAOException("Error al crear el alumno ==> "+ex.getMessage());
        }
    }

    @Override
    public Alumno read(Long dni) throws DAOException {
        try {
            raf.seek(0); // Se posiciona al inicio
            String linea;
            String[] campos; 
            while((linea = raf.readLine())!=null) {
                campos = linea.split(persona.Persona.DELIM); 
                if (campos[0].trim().equals(String.valueOf(dni))) { 
                    return str2Alu(campos);
                }
            }
        } catch (IOException | MiCalendarioException | PersonaException ex) {
            Logger.getLogger(AlumnoDAOTXT.class.getName()).log(Level.SEVERE, null, ex);
            throw new DAOException("Erro al leer ==>"+ ex.getMessage());
        }
        
        return null;
    }

    private Alumno str2Alu(String[] campos) throws MiCalendarioException, PersonaException, NumberFormatException {
        int i=0;
        Long dniAlu= Long.valueOf(campos[i++].trim());
        String nombre=campos[i++].trim();
        String apellido=campos[i++].trim();
       
        String[] fecha= campos[i++].split("/");
        MiCalendario fechaNac = new MiCalendario(Integer.valueOf(fecha[0].trim()), Integer.valueOf(fecha[1].trim()), Integer.valueOf(fecha[2].trim()));
       
        char sexo= campos[i++].trim().charAt(0);
       
        fecha= campos[i++].split("/");
        MiCalendario fechaIng = new MiCalendario(Integer.valueOf(fecha[0].trim()), Integer.valueOf(fecha[1].trim()), Integer.valueOf(fecha[2].trim()));
               
        Integer cantMatAprob= Integer.valueOf(campos[i++].trim());
        Double promedio= Double.valueOf(campos[i++].trim().replace(",", ".") );
       
        return new Alumno(dniAlu,nombre,apellido,fechaNac,fechaIng,cantMatAprob,promedio,sexo);
    }

    @Override
    public void update(Alumno entidad) throws DAOException{
            //raf.getfilepointer   => devuleve el puntero con su valor actual  
           //raf.seek(puntero) actualizar toda la linea
     }
    /**
     * Es una baja logica
     * @param dni
     * @throws DAOException 
     */
    @Override
    public void delete(Long dni) throws DAOException {
        Alumno alu= read(dni);
        if(alu==null){
        throw new DAOException("El alumno a eliminar no existe");
        }
        alu.setActivo(false);
        update(alu);
    }

    @Override
    public boolean existe(Long dni) throws DAOException {
        /*try {
            raf.seek(0); // Se posiciona al inicio
            String linea;
            String[] campos; 
            while((linea = raf.readLine())!=null) {
                campos = linea.split(persona.Persona.DELIM);
//                if (Long.valueOf(campos[0].trim()).equals(dni)) {
                if (campos[0].trim().equals(String.valueOf(dni))) {
                //if (campos[0].trim().equals(dni.toString())) {
                    return true;
                }
            }
        } catch (IOException ex) {
            Logger.getLogger(AlumnoDAOTXT.class.getName()).log(Level.SEVERE, null, ex);
        }
        */
        return read(dni)!=null;
    }

    @Override
    public List<Alumno> findAll() throws DAOException {
   List<Alumno> alumnos = new ArrayList<>();
   
   try {
            raf.seek(0); // Se posiciona al inicio
            String linea;
            String[] campos; 
            Alumno alu;
            while((linea = raf.readLine())!=null) {
                campos = linea.split(persona.Persona.DELIM);
                alu= str2Alu(campos);
                alumnos.add(alu);
                //min hora 1:26
            }
        } catch (IOException | MiCalendarioException | PersonaException | NumberFormatException ex) {
            Logger.getLogger(AlumnoDAOTXT.class.getName()).log(Level.SEVERE, null, ex);
            throw new DAOException("Error al leer el Alumno"+ ex.getMessage());
        }
   return alumnos;
    }
    
     
}
