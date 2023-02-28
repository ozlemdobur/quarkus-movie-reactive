package org.gs.movie;


import javax.persistence.*;


@Table(name = "movie")
@Entity
public class MovieEntity {
    @Id
    @GeneratedValue(generator = "seq_movie")
    @SequenceGenerator(name = "seq_movie", sequenceName = "seq_movie", allocationSize = 1, initialValue = 1)
    private Long id;
    @Column(length = 100)
    private String title;
    @Column(length = 200)
    private String description;
    private String country;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

}
