<div id="loginform" title="Login" class="messagedialog">
    <form>
        <fieldset class="centered loginfieldset">
            <label for="email">Email</label>
            <input type="text" name="email" id="loginemail" value="" class="logininput" />
            <label for="loginpassword">Password</label>
            <input type="password" name="password" id="loginpassword" value="" class="logininput" />
            <br>
            <label for="forget">Forgot your password?</label>
            <input type="button" name="forget" id="loginforget" value="Reset password" class="logininput" />

        </fieldset>
    </form>
</div>
<script type="text/javascript" src="https://ajax.googleapis.com/ajax/libs/jquery/1.7.1/jquery.min.js"></script>
<script type="text/javascript" src="asset/js/md5-min.js"></script>
<script type="text/javascript">
    $("#loginforget").click(resetPassword);
    function doLogin()
    {
        $.get("crypt", null, function(data) {
            eval(data);
            var pwd = hex_md5($('#loginpassword').val());
            pwd = this.crypt(pwd);
            $.getJSON("login/dologin", {
                "email": $("#loginemail").val(),
                "pwd": pwd
            }, function(data) {
                if (this.autologin)
                {
                    this.autologin.completed(data.success);
                }
                else
                {
                    if (!data.success)
                        alert(data.message);
                    else
                        location.reload(true);
                }
            });
        }, "text");
    }

    function resetPassword()
    {
        if (confirm("Are you sure to reset your password?"))
        {
            var mail = $("#loginemail").val();
            $.getJSON("/servlet/commandExecutor", {"cmd": "resetpwd", "email": mail}, function(data) {
                if (data.success === true)
                {
                    location.href = "resetpwdsubmitted.jsp?mail=" + encodeURIComponent(mail);
                }
                else
                {
                    alert(data.message);
                    location.reload(true);
                }

            });
        }
    }
    if (this.autologin)
    {
        $("#loginemail").val(this.autologin.getUser());
        $("#loginpassword").val(this.autologin.getPassword());
        try
        {
            doLogin();
        }
        catch (e)
        {
            this.autologin.completed(false);
        }
    }

</script>


