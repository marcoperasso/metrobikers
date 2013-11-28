<script type="text/javascript">setActiveTab('my_ecommuters');</script>

<div class="modal fade" id="findECOmmuterModal" tabindex="-1" role="dialog" aria-labelledby="myModalLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
                <h4 class="modal-title">Trova ECOmmuters</h4>
            </div>
            <div class="modal-body">
                <form id="findform" autocomplete="on" method="post"> 
                    <fieldset>
                        <input type="text" name="ecommutername" id="ecommutername" autofocus placeholder="Scrivi qui il nome" class="required form-control"/><br>
                    </fieldset>
                </form>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-default" id="doconnect">Chiedi di entrare nei tuoi ECOmmuter</button>
            </div>
        </div><!-- /.modal-content -->
    </div><!-- /.modal-dialog -->
</div><!-- /.modal -->
<script type="text/javascript">

    function datasource(request, response)
    {
        function onServerResponse(data)
        {
            response($.map(data.users, function(usr) {
                return {
                    label: usr.name + ' ' + usr.surname,
                    value: usr.name + ' ' + usr.surname,
                    id: usr.id
                };
            }));
        }
        $.getJSON("/user//get_users_by_name_filter", {'filter': request.term}, onServerResponse);
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
            var input = $('#ecommutername');
            window.location.href = "/user/connect?id=" + encodeURIComponent(input[0].userid) + '&fullname=' + encodeURIComponent(input.val());
    });
</script>
<div class="col-md-1"></div>
<div class="col-md-10">
    <?php if (count($linkedusers) == 0) { ?>
        <h2>Ma come! Non hai ancora contattato alcun ECOmmuter? <a data-toggle="modal" class="btn btn-primary btn-lg" title="Cerca altri ECOmmuters" href="#findECOmmuterModal">Fallo adesso!</a></h2>
    <?php } else { ?>
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

