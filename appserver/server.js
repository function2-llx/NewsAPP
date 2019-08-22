const express = require('express')
const path = require('path')
const app = express()
const url = require('url')
const fs = require('fs')
var bodyParser = require('body-parser')

app.use(express.static(path.join(__dirname, 'site')))
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({//处理以form表单的提交
    extended: true
}))

app.listen(8080, () => {
    console.log('App listening at port 8080')
})

app.post('/', (req, res) => {
    data = req["body"]
    console.log(data instanceof Object)
    res.json({ lqb: "woyezheyangjuede" })
})

// app.get('/get', (req, res) => {
//     console.log(req.url)
//     console.log(url.parse(req.url, true).query)
//     res.send(fs.readFileSync('site/index.html').toString())
//     // res.send('test')
    
//     // res.end()
// })

// app.get('/', (req, res, next) => {
//     console.log('in get /', req.url)
// })

// const http =require('http')
// const url = require('url')
// const fs = require('fs')

// http.createServer(function(req, res) {
//   if (req.method == 'GET') {
//     console.log(req.url)
//     data = url.parse(req.url, true).query
//     console.log(data)

//     res.statusCode = 200
//     res.write(fs.readFileSync('site/index.html'))
//     res.end();
//   }
//   else { // POST
//     var data = '';
//     req.on('data', (chunk) => { data += chunk; });
//     console.log(data)
//   }
// }).listen(8080);