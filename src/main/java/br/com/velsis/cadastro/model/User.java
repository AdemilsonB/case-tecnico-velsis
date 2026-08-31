package br.com.velsis.cadastro.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidade que representa a tabela users. Os nomes das colunas seguem
 * literalmente o enunciado do case, por isso o endereço fica achatado aqui
 * em vez de virar uma tabela separada.
 */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    // Guardado apenas com dígitos; a máscara é responsabilidade do frontend
    @Column(name = "document", nullable = false, length = 14, unique = true)
    private String document;

    @Column(name = "address_line", nullable = false, length = 150)
    private String addressLine;

    @Column(name = "address_number", nullable = false, length = 10)
    private String addressNumber;

    @Column(name = "city", nullable = false, length = 80)
    private String city;

    // A coluna é CHAR(2) no schema; sem este tipo o Hibernate esperaria VARCHAR e falharia no validate
    @JdbcTypeCode(SqlTypes.CHAR)
    @Column(name = "state", nullable = false, length = 2)
    private String state;

    @Column(name = "zip", nullable = false, length = 8)
    private String zip;

    // Datas de controle são responsabilidade do Hibernate, nunca do formulário
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDate getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getAddressLine() {
        return addressLine;
    }

    public void setAddressLine(String addressLine) {
        this.addressLine = addressLine;
    }

    public String getAddressNumber() {
        return addressNumber;
    }

    public void setAddressNumber(String addressNumber) {
        this.addressNumber = addressNumber;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
