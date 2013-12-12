<script type="text/javascript">
    setActiveTab('user');
    setUpdateUrl("/user/update");
</script>
<div class="col-md-1"></div>
<div class="col-md-10">
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
        <b>La mia posizione può essere visualizzata da: </b><span name="positionpolicy" class="changeable enumcontent" items="<?php echo position_policy_items(); ?>"><?php echo decode_position_policy($user->positionpolicy); ?></span><br/>
    </fieldset>


</div>
<div class="col-md-1"></div>
