<!DOCTYPE html>
<html lang="en">
    <head>
        <?php $this->load->view('templates/header'); ?>
        <title></title>
    </head>

    <body>
        <?php $this->load->view('templates/publicvisualheader'); ?>
        <div class="row">
            <div class="col-md-3">
                
            </div>

            <div class="col-md-6">
                <div class="container">
                    <br />
                    <h2 class="text-center">Chiave di attivazione non valida</h2><br />
                    <div class="col-md-2">
                
                    </div>
        
                    <div class="col-md-8">
                    <p class="text-center">
                        Chiave di attivazione non valida: <?php echo $key?>
                    </p>
                    </div>
                    <div class="col-md-2">
                        
                    </div>
                </div>
            </div>
            <div class="col-md-3">
                
            </div>
            
        </div>
        <?php $this->load->view('templates/footer'); ?>
    </body>
</html>