<!DOCTYPE html>
<html lang="en">
    <head>
        <?php include '/application/views/templates/header.php'; ?>
        <title>Errore</title>
    </head>

    <body>
        <div class="contenta">
            
            <div class="row body center-text">
                <div class="col-md-2"></div>
                <div class="col-md-8">
                <h1><?php echo $heading; ?></h1>
                <img class="centered" src="/asset/img/crashedfrog.png"/>
                <?php echo $message; ?>
                </div>
                <div class="col-md-2"></div>
            </div>
            <?php include '/application/views/templates/footer.php'; ?>
        </div>
    </body>
</html>