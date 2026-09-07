// js/login.js
document.getElementById('loginBtn').addEventListener('click', function(e) {
    e.preventDefault();

    const emailInput = document.getElementById('email').value;
    const passwordInput = document.getElementById('password').value;

    // Retrieve saved user data from localStorage using email as key
    const savedUserJSON = localStorage.getItem(emailInput);

    if (!savedUserJSON) {
        alert('No account found with this email. Please Sign Up first.');
        return;
    }

    const savedUser = JSON.parse(savedUserJSON);

    // Verify password
    if (savedUser.password === passwordInput) {
        alert('Login Successful!');
        
        // Save current active login session
        localStorage.setItem('currentUser', JSON.stringify(savedUser));

        // Redirect to security dashboard
        window.location.href = 'security.html';
    } else {
        alert('Incorrect password! Please try again.');
    }
});