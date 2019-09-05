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

function internalError(res) {
    res.statusCode = 50
    res.end()
}

app.get('/favorite/get', (req, res) => {
    console.log(req.url)

    data = url.parse(req.url, true).query
    favorite.serialize(() => {
        favorite.run(`create table if not exists [${data.id}] (
            id text primary_key,
            json text
        )`, (err) => {
            if (err) {
                console.log('233')
                console.error(err)
                res.sendStatus(500)
            } else {
                console.log(`user login: ${data.id}`)
                favorite.all(`select * from [${data.id}]`, (err, rows) => {
                    if (err) {
                        console.error(err)
                        res.sendStatus(500)
                    } else {
                        console.log
                        let result = []
                        rows.forEach((row) => {
                            console.log(row.id)
                            result.push(JSON.parse(row.json))
                        })
                        console.log('parse over')
                        res.json({data: result})
                    }
                })
            }
        })
    })
})

app.post('/favorite/insert', (req, res) => {
    data = req.body
    news = JSON.parse(data.newsJson)
    console.log(`insert ${news.newsID}:${news.title} of user ${data.id}`)
    favorite.serialize(() => {
        favorite.run(`insert into [${data.id}] values(?, ?)`, [news.newsID, req.body.newsJson], function(err) {
            if (err) {
                console.log(err)
                res.sendStatus(500)
            } else {
                console.log('insert success')
                res.end()
            }
        })
    })
})

app.post('/favorite/remove', (req, res) => {
    data = req.body
    news = JSON.parse(data.newsJson)
    console.log(`remove ${news.newsID}:${news.title} of user ${data.id}`)
    favorite.serialize(() => {
        favorite.run(`delete from [${data.id}] where id == ?`, [news.newsID],function(err) {
            if (err) {
                console.log(err)
                res.sendStatus(500)
            } else {
                console.log('remove success')
                res.end()
            }
        })
    })
})