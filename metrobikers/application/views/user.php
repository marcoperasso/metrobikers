
<style type="text/css">
    img.changeable
    {
        min-width: 100px;
    }
</style>
<div class="col-md-1"></div>
<div class="col-md-10">
    <fieldset>
        <legend>Foto</legend>
        <img class="changeable imagecontent" alt="Foto" src="<?php echo get_user_photo($user->id) ?>" style="width: 100px; height: 100px"/>
    </fieldset>
    <fieldset>
        <legend>Dati anagrafici</legend>
        <b>Nome:</b> <span name="name" class="changeable"><?php echo htmlSpaceIfEmpty($user->name); ?></span><br/>
        <b>Cognome:</b>  <span name="surname" class="changeable"><?php echo htmlSpaceIfEmpty($user->surname); ?></span><br/>
        <b>Nickname:</b>  <span name="nickname" class="changeable"><?php echo htmlSpaceIfEmpty($user->nickname); ?></span><br/>
        <b>Email:</b>  <span name="email"><?php echo $user->mail; ?></span><br/>
        <b>Data di nascita: </b><span name="birthdate" class="changeable datecontent"><?php echo Date("d/m/Y", strtotime($user->birthdate)); ?></span><br/>
        <b>Sesso:</b>  <span name="gender" class="changeable enumcontent" items="<?php echo gender_items(); ?>"><?php echo decode_gender($user->gender); ?></span><br/>
    </fieldset>
    <br>
    <fieldset>
        <legend>Privacy</legend>
        <b>Chi può visualizzare la mia posizione: </b><span name="showposition" class="changeable enumcontent" items="<?php echo showposition_items(); ?>"><?php echo decode_showposition($user->showposition); ?></span><br/>
        <b>Chi può visualizzare il mio nome: </b><span name="showname" class="changeable enumcontent" items="<?php echo showname_items(); ?>"><?php echo decode_showname($user->showname); ?></span><br/>
    </fieldset>


</div>
<div class="col-md-1"></div>
<script type="text/javascript">
    setActiveTab('user');
    setUpdateUrl("/user/update");
    $("input[type='file']").change(function() {
        $(this).closest('form').submit();
    });
</script>