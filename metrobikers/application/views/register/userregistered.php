<!DOCTYPE html>
<html lang="en">
    <head>
        <?php $this->load->view('templates/header'); ?>
        <title>User registered</title>
    </head>
    
    <body>
        <?php $this->load->view('templates/publicvisualheader'); ?>

        <div class="row">
            <div class="col-md-3">
                
            </div>

            <div class="col-md-6">
                <div class="container">
                    <br />
                    <h2 class="text-center">Primo passo compiuto!</h2><br />
                    <div class="col-md-2">
                
                    </div>
        
                    <div class="col-md-8">
                    <p class="text-center">
                        Grazie <?php echo $user->name?> per esserti registrato. <br />
                        Segui le istruzioni che ti abbiamo mandato via email all'indirizzo <?php echo $user->mail?> per completare la tua registrazione.
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