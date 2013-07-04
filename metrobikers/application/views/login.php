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
<script type="text/javascript" src="<?php echo base_url() ?>asset/js/md5-min.js"></script>
<script type="text/javascript">
    $("#loginforget").click(resetPassword);
    var _autologin = this.autologin;
    function doLogin()
    {
        doLoginInternal();
    }
    function doLoginInternal(onEnd)
    {
        jQuery.get("<?php echo base_url() ?>crypt", null, function(data) {
            eval(data);
            var pwd = hex_md5($('#loginpassword').val());
            pwd = this.crypt(pwd);
            $.getJSON("<?php echo base_url() ?>login/dologin", {
                "email": $("#loginemail").val(),
                "pwd": pwd
            }, function(data) {
                if (onEnd)
                {
                    onEnd(data)
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
    if (_autologin)
    {
        $("#loginemail").val(_autologin.getUser());
        $("#loginpassword").val(_autologin.getPassword());
        try
        {
            doLoginInternal(function(data) {
                _autologin.completed(data.success, data.message);
            });
        }
        catch (e)
        {
            _autologin.completed(false, e);
        }
    }

</script>


