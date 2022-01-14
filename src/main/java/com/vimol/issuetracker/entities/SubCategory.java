package com.vimol.issuetracker.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;

@Entity
public class SubCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String catName;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JsonBackReference(value = "subCategories")
    private Category category;

    @JsonIgnore
    private String subCatErr;

    public String getSubCatErr() {
        return subCatErr;
    }

    public void setSubCatErr(String subCatErr) {
        this.subCatErr = subCatErr;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }
}
