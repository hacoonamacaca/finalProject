package tw.com.ispan.eeit.model.entity;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.com.ispan.eeit.model.entity.store.StoreBean;

@Data
@Entity
@Table(name = "owner")
@NoArgsConstructor
public class OwnerBean {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String email;

    private String password;

    private String name;

    private String phone;

    @Column(name = "sigup_date")
    private LocalDateTime signupDate;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "is_active")
    private Boolean isActive;

    @Column(name = "is_verify")
    private Boolean isVerify;

    @OneToMany(mappedBy = "owner")
    private List<StoreBean> stores;
}
