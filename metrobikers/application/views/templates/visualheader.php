<table cellpadding="0" cellspacing="0" border="0" class ="heading">
    <tr>
        <td style="width:25%;"></td>
        <td style="width:50%;">
            <a href="/" title="Vai alla pagina principale">
                <img src="asset/img/logo.png" id="ecommuter_logo" class="logo">
            </a>
        </td>
        <td style="width:25%;"><?php
            $CI = & get_instance();
            $CI->load->library('session');
            $CI->load->model('User_model');
            $user = unserialize($CI->session->userdata("user"));
            if ($user == NULL) {
                ?>
                <a class ="logincommand" href="javascript:void(0)" title="Entra"><img class="headercommand" src="asset/img/enter.png" alt="Entra"/></a>
                <a class ="registercommand" href="register" title="Diventa un ECOmmuter!"><img class="headercommand" src="asset/img/register.png" alt="Diventa un ECOmmuter!"/></a>
            <?php } else { ?>
                <span>Benvenuto, <?php echo $user->to_string(); ?></span>
                <a class ="logoffcommand" href="javascript:void(0)" title="Esci"><img class="headercommand" src="asset/img/exit.png" alt="Esci"/></a>
            <?php } ?></td>

    </tr>
</table>
<script type="text/javascript">
    function login()
    {
        var loginForm = $("#loginform");
        if (loginForm.length !== 0)
        {
            loginForm.dialog("open");
            return;
        }
        $.get("login", null, function(data) {
            loginForm = $(data)
                    .appendTo(document.body)
                    .dialog({
                autoOpen: true,
                width: 400,
                modal: true,
                buttons: {
                    Login: doLogin,
                    Cancel: function() {
                        $(this).dialog("close");
                    }
                },
                close: function() {

                }
            })
                    .keydown(function(e) {
                if (e.keyCode === 13)
                {
                    e.preventDefault();
                    e.stopPropagation();
                    doLogin();
                }
            });
        });
    }

    function logoff()
    {
        $.getJSON("login/dologoff", {}, function(data) {
            if (data.success === true)
                location.reload(true);
            else
                alert(data.message);
        });
    }
    $(".logincommand").click(login);
    $(".logoffcommand").click(logoff);

</script>