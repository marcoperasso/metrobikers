
<div class="col-md-1"></div>
<div class="col-md-10">
    <fieldset>
        <legend>Dati anagrafici</legend>
        Nome: <span name="name" class="changeable"><?php echo $user->name; ?></span><br/>
        Cognome:  <span name="surname" class="changeable"><?php echo $user->surname; ?></span><br/>
        Email:  <span name="email"><?php echo $user->mail; ?></span><br/>
        Data di nascita:  <span name="birthdate" class="changeable datecontent"><?php echo Date("d/m/Y", strtotime($user->birthdate)); ?></span><br/>
        Sesso:  <span name="gender" class="changeable gendercontent"><?php echo decode_gender($user->gender); ?></span><br/>
    </fieldset>
</div>
<div class="col-md-1"></div>
