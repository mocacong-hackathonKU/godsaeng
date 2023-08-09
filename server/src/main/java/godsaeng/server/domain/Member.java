package godsaeng.server.domain;

import godsaeng.server.exception.badrequest.InvalidNicknameException;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.regex.Pattern;

@Entity
@Table(name = "member", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"email", "platform"})
})
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTime {

    private static final Pattern NICKNAME_REGEX = Pattern.compile("^[a-zA-Zㄱ-ㅎㅏ-ㅣ가-힣]{2,6}$");
    private static final int REPORT_MEMBER_THRESHOLD_COUNT = 11;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name="password")
    private String password;

    @Column(name = "nickname", unique = true)
    private String nickname;

    @OneToOne
    @JoinColumn(name = "member_profile_image_id")
    private MemberProfileImage memberProfileImage;

    @Enumerated(EnumType.STRING)
    @Column(name = "platform")
    private Platform platform;

    @Column(name = "platform_id")
    private String platformId;

    public Member(String email, String password, String nickname, MemberProfileImage memberProfileImage,
                  Platform platform, String platformId) {
        validateNickname(nickname);
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.memberProfileImage = memberProfileImage;
        this.platform = platform;
        this.platformId = platformId;
    }

    public Member(String email, Platform platform, String platformId) {
        this.email = email;
        this.platform = platform;
        this.platformId = platformId;
    }

    public Member(String email, MemberProfileImage memberProfileImage, Platform platform, String platformId) {
        this.email = email;
        this.memberProfileImage = memberProfileImage;
        this.platform = platform;
        this.platformId = platformId;
    }

    public Member(String email, String password, String nickname) {
        this(email, password, nickname, null, Platform.GODSAENG, null);
    }

    public void registerOAuthMember(String email, String nickname) {
        validateNickname(nickname);
        this.nickname = nickname;
        if (email != null) {
            this.email = email;
        }
    }

    private void validateNickname(String nickname) {
        if (!NICKNAME_REGEX.matcher(nickname).matches()) {
            throw new InvalidNicknameException();
        }
    }

    public String getImgUrl() {
        return this.memberProfileImage != null ? this.memberProfileImage.getImgUrl() : null;
    }
}
