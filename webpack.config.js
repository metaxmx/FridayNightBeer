var webpack = require('webpack');
var path = require('path');
var fs = require('fs');
var ExtractTextPlugin = require("extract-text-webpack-plugin");

// Webpack Config
var webpackConfig = {
    entry: {
        'polyfills':       './frontend/bundle/polyfills.ts',
        'vendor':          './frontend/bundle/vendor.ts',
        'app':             './frontend/bundle/app.ts',
        'vendor-style':    './frontend/bundle/vendor-style.ts',
        'app-style':       './frontend/bundle/app-style.ts'
    },

    devtool: 'source-map',

    cache: true,
    output: {
        path: 'public/dist',
        filename: '[name].bundle.js',
        sourceMapFilename: '[name].map',
        chunkFilename: '[id].chunk.js'
    },

    resolve: {
        extensions: ['', '.ts', '.js']
    },

    plugins: [
        new webpack.optimize.CommonsChunkPlugin({ name: ['app', 'vendor', 'polyfills'], minChunks: Infinity }),
        new ExtractTextPlugin("[name].css"),
        new webpack.optimize.DedupePlugin(),
        function() {
            this.plugin("done", function(stats) {
                //console.log("STATS:", stats.toJson())
                //fs.writeFileSync('./stats.json', JSON.stringify(stats.toJson()));
            });
        }
    ],

    module: {
        loaders: [
            // .ts files for TypeScript
            { test: /\.ts$/, loaders: ['awesome-typescript-loader', 'angular2-template-loader'] },
            { test: /\.css$/, loader: ExtractTextPlugin.extract("style-loader", "css-loader") },
            { test: /\.less$/, loader: ExtractTextPlugin.extract("style-loader", "css-loader!less-loader") },
            { test: /\.eot(\?v=\d+\.\d+\.\d+)?$/, loader: "file" },
            { test: /\.(woff|woff2)(\?v=\d+\.\d+\.\d+)?$/, loader:"url?prefix=font/&limit=5000" },
            { test: /\.ttf(\?v=\d+\.\d+\.\d+)?$/, loader: "url?limit=10000&mimetype=application/octet-stream" },
            { test: /\.svg(\?v=\d+\.\d+\.\d+)?$/, loader: "url?limit=10000&mimetype=image/svg+xml" },
            { test: /\.html$/, loader: 'html' },
            { test: /\.json$/, loader: 'json' },
            { test: /\.(jpe?g|png|gif)$/i, loaders: ['file?hash=sha512&digest=hex&name=[hash].[ext]', 'image-webpack?bypassOnDebug&optimizationLevel=7&interlaced=false'] }
        ]
    }

};

var webpackMerge = require('webpack-merge');
module.exports = webpackMerge(webpackConfig);