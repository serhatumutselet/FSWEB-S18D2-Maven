package com.workintech.sqldmlprocedures;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootApplication
public class SqlDmlProceduresApplication {

	public static void main(String[] args) {
		SpringApplication.run(SqlDmlProceduresApplication.class, args);
	}

	@Bean
	public CommandLineRunner setupLibraryTasks(JdbcTemplate jdbcTemplate) {
		return args -> {
			jdbcTemplate.execute("""
					INSERT INTO tur(ad)
					SELECT 'Biyografi'
					WHERE NOT EXISTS (SELECT 1 FROM tur WHERE ad = 'Biyografi')
					""");

			jdbcTemplate.execute("""
					INSERT INTO yazar(ad, soyad)
					SELECT 'Nurettin', 'Belek'
					WHERE NOT EXISTS (
					    SELECT 1 FROM yazar WHERE ad = 'Nurettin' AND soyad = 'Belek'
					)
					""");

			jdbcTemplate.execute("UPDATE ogrenci SET sinif = '10C' WHERE sinif = '10B'");
			jdbcTemplate.execute("UPDATE kitap SET puan = puan + 5");
			jdbcTemplate.execute("DELETE FROM yazar WHERE ad = 'Mehmet'");

			jdbcTemplate.execute("""
					INSERT INTO tur(ad)
					SELECT 'Kişisel Gelişim'
					WHERE NOT EXISTS (SELECT 1 FROM tur WHERE ad = 'Kişisel Gelişim')
					""");

			jdbcTemplate.execute("""
					UPDATE kitap
					SET turno = (SELECT turno FROM tur WHERE ad = 'Kişisel Gelişim')
					WHERE ad = 'Benim Üniversitelerim'
					""");

			jdbcTemplate.execute("""
					CREATE OR REPLACE FUNCTION ogrencilistesi()
					RETURNS TABLE (
					    ogrno bigint,
					    ad character varying(45),
					    soyad character varying(45),
					    cinsiyet character varying(1),
					    sinif character varying(3),
					    puan integer,
					    dtarih character varying(20)
					)
					LANGUAGE sql
					AS $$
					    SELECT o.ogrno, o.ad, o.soyad, o.cinsiyet, o.sinif, o.puan, o.dtarih
					    FROM ogrenci o
					    ORDER BY o.ogrno
					$$
					""");

			jdbcTemplate.execute("""
					CREATE OR REPLACE PROCEDURE ekle(
					    IN p_ad character varying(45),
					    IN p_puan integer,
					    IN p_yazarno bigint,
					    IN p_turno bigint
					)
					LANGUAGE plpgsql
					AS $$
					BEGIN
					    INSERT INTO kitap(ad, puan, yazarno, turno)
					    VALUES (p_ad, p_puan, p_yazarno, p_turno);
					END;
					$$
					""");

			jdbcTemplate.execute("""
					CREATE OR REPLACE PROCEDURE sil(IN p_ogrno bigint)
					LANGUAGE plpgsql
					AS $$
					BEGIN
					    DELETE FROM ogrenci WHERE ogrno = p_ogrno;
					END;
					$$
					""");
		};
	}
}
