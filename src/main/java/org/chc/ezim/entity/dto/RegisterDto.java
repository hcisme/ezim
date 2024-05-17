package org.chc.ezim.entity.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import org.chc.ezim.entity.constants.Constants;

public class RegisterDto {

    @NotEmpty
    private String captchaKey;

    @NotEmpty
    @Email
    @Size(max = 50)
    private String email;

    @NotEmpty
    @Size(max = 20)
    private String nickName;

    @NotEmpty
    @Size(min = 6, max = 32)
    @Pattern(regexp = Constants.REGEX_PASSWORD)
    private String password;

    @NotEmpty
    private String captcha;

    public String getCaptchaKey() {
        return captchaKey;
    }

    public void setCaptchaKey(String captchaKey) {
        this.captchaKey = captchaKey;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    @Override
    public String toString() {
        return "RegisterDto{" +
                "captchaKey='" + captchaKey + '\'' +
                ", email='" + email + '\'' +
                ", nickName='" + nickName + '\'' +
                ", password='" + password + '\'' +
                ", captcha='" + captcha + '\'' +
                '}';
    }
}
