
<div class="col-md-12">
    <h3>I miei dati</h3>
    Nome: <?php echo $user->name; ?><br/>
    Cognome: <?php echo $user->surname; ?><br/>
    Email: <?php echo $user->mail; ?><br/>
    Data di nascita: <?php echo $user->birthdate; ?><br/>
    Sesso: <?php echo decode_gender($user->gender); ?><br/>
</div>
