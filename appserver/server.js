const express = require('express')
const path = require('path')
const app = express()
const url = require('url')
const fs = require('fs')
const mkdirp = require('mkdirp')
let bodyParser = require('body-parser')
const superagent = require('superagent')
const sqlite3 = require('sqlite3').verbose();


app.use(express.static(path.join(__dirname, 'public')))
app.use(bodyParser.json());
app.use(bodyParser.urlencoded({//处理以form表单的提交
    extended: true
}))

port = 8080

app.listen(port, () => {
    console.log('App listening at port', port)
})

app.get('/news', (req, appres) => {
    console.log(req.url)
    data = url.parse(req.url, true).query
    superagent.get('https://api2.newsminer.net/svc/news/queryNewsList')
        .query({startDate: data.publishTime, endDate: data.publishTime, size: 50})
        .end((err, res) => {
            for (let news of res.body.data) {
                if (news.newsID == data.newsID) {
                    appres.render("index.ejs", {news: news})
                    return
                }
            }
            appres.redirect('/notfound.html')
        })
})

mkdirp.sync('db')
let favorite = new sqlite3.Database('db/favorite.db', (err) => {
    if (err) {
        return console.error(err.message)
    }
    console.log('Connected to database: favorite');
})

app.get('/database/favorite', (req, res) => {
    console.log(req.url)
    data = url.parse(req.url, true).query

})
