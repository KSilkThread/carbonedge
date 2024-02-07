const express = require('express');
const fs = require('fs');
const app = express();
const port = 3000;

let loggedInOrg = null;

app.post('/login/:org', (req, res) => {
    const org = req.params.org;

    if (org !== 'org0' && org !== 'org1') {
        res.status(400).send({ error: 'Invalid organization specified. Please use org0 or org1.' });
        return;
    }

    loggedInOrg = org;
    res.send(`Logged in as ${org} successfully!`);
});

app.get('/api/transponder', (req, res) => {
    if (!loggedInOrg) {
        res.json({ securityID: "" });
        return;
    }

    const filePath = `profiles/${loggedInOrg}.example.com/Admin.id`;

    fs.readFile(filePath, 'utf8', (err, data) => {
        if (err) {
            res.status(500).send({ error: `Error reading the ${filePath} file` });
            return;
        }
        res.json({ securityID: data });
    });
});

app.listen(port, () => {
    console.log(`Server is running on http://localhost:${port}`);
});
