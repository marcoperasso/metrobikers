<script type="text/javascript">setActiveTab('my_ecommuters');</script>

<div class="modal fade" id="findECOmmuterModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title">Trova ECOmmuters</h4>
            </div>
            <div class="modal-body">
                <form id="findform" autocomplete="on" method="post" onsubmit="return false;" > 
                    <fieldset>
                        <input type="text" name="ecommutername" id="ecommutername" autofocus placeholder="Scrivi qui il nome" class="required form-control"/><br>
                    </fieldset>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" id="doconnect">Chiedi di entrare nel tuo gruppo</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
<script type="text/javascript">

    function datasource(request, response)
    {
        $.getJSON("/user/get_not_linked_users", {'filter': request.term}, function(data)
        {
            response($.map(data.users, function(usr) {
                return {
                    label: usr.name + ' ' + usr.surname,
                    value: usr.name + ' ' + usr.surname,
                    id: usr.id
                };
            }));
        });
    }
    function select(event, ui)
    {
        this.userid = ui.item.id;
    }
    $("#ecommutername").autocomplete({
        source: datasource,
        select: select
    });
    $("#doconnect").click(function() {
        if (testFields($("#findform")))
        {
            var input = $('#ecommutername');
            //l'utente ha cliccato un hint, quindi so già l'id dell'ecommuter
            if (input[0].userid)
            {
                window.location.href = "/user/connect/" + input[0].userid;
                return;
            }
            //l'utente ha solo scritto nella text box, devo recoperare l'id dell'ecommuter
            $.getJSON("/user/get_not_linked_users", {'filter': input.val()}, function(data) {
                if (data.users.length > 1)
                {
                    alert("Più di un ECOmmuter trovato per i criteri impostati.");
                    input.focus();
                    return;
                }
                if (data.users.length === 0)
                {
                    alert("Nessun ECOmmuter trovato per i criteri impostati.");
                    input.focus();
                    return;
                }
                window.location.href = "/user/connect/" + data.users[0].id;
            });
        }
    });


</script>
<div class="col-md-1"></div>
<div class="col-md-10">
    <?php if (count($linkedusers) == 0) { ?>
        <h1>Il gruppo è vuoto.</h1>
        <h2>Ma come! Ancora non hai contattato altri ECOmmuters? <a data-toggle="modal" class="btn btn-primary btn-lg" title="Cerca altri ECOmmuters" href="#findECOmmuterModal">Fallo adesso!</a></h2>
    <?php } else { ?>
        <div class="col-md-10"></div>
        <div class="col-md-2"><a data-toggle="modal" class="btn btn-default btn-lg" title="Cerca altri ECOmmuters" href="#findECOmmuterModal">Cerca altri ECOmmuters</a>
        </div>
        <table class ="table table-striped">
            <thead>
                <tr>
                    <th>Nome</th>
                    <th>Cognome</th>
                </tr>
            </thead>
            <tbody>
                <?php
                foreach ($linkedusers as $linkeduser) {
                    ?>
                    <tr>
                        <td><?php echo $linkeduser->name; ?></td>
                        <td><?php echo $linkeduser->surname; ?></td>
                    </tr>
                    <?php
                }
                ?>
            </tbody>
        </table>
    <?php } ?>
</div>
<div class="col-md-1"></div>

